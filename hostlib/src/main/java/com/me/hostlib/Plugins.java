package com.me.hostlib;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.me.hostlib.plugin.ActivityCache;
import com.me.hostlib.plugin.ManifestParser;
import com.me.hostlib.plugin.ProviderService;
import com.me.hostlib.plugin.ReceiverParser;
import com.me.hostlib.plugin.ServiceCache;
import com.me.hostlib.plugin.SlotManager;
import com.me.hostlib.utils.PackageArchiveData;
import com.me.hostlib.utils.PatchClassLoaderUtils;
import com.me.hostlib.utils.ReflectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dalvik.system.DexClassLoader;

public class Plugins {
    public static final int PLUGIN_INSTALL = 65534;
    private static volatile Plugins sPlugins;
    private static HashMap<String, Context> allContext = new HashMap<>();
    private Handler handler;
    private HashMap<String, ClassLoader> mClassLoader = new HashMap<>();
    private boolean mPatchClassLoader = false;
    private boolean mLoadPlugin = false;
    private Context initContext;
    private ArrayList<String> cmd = new ArrayList<>();
    private ArrayList<String> changed = new ArrayList<>();

    private Plugins() {
    }

    private static String tag() {
        return "Plugins p:" + Plugins.getInstance().getProcessName() + " t:" + Thread.currentThread().getName();
    }

    public static Plugins getInstance() {
        if (sPlugins == null) {
            synchronized (Plugins.class) {
                if (sPlugins == null) {
                    sPlugins = new Plugins();
                }
            }
        }
        return sPlugins;
    }

    public static void init(final Application application) {
        Plugins.getInstance().initContext = application;
        boolean b = PatchClassLoaderUtils.patch(application);
        if (b) {
            Log.i(tag(), "Patch ClassLoader success");
        } else {
            Log.i(tag(), "Patch ClassLoader false");
        }
    }

    private static void handlePackages(Application context, PackageArchiveData p) {
        Log.i(tag(), "HandlePackages");
        try {
            DexClassLoader classLoader = new DexClassLoader(p.getDexPath(), p.getOpt(), p.getLibs(), context.getClassLoader().getParent());
            Plugins.getInstance().installClassLoader(p.getPluginName(), classLoader);

            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(p.getDexPath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS);
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = p.getDexPath();
            appInfo.publicSourceDir = p.getDexPath();

            Resources resources = context.getPackageManager().getResourcesForApplication(appInfo);

            ActivityInfo[] activities = packageInfo.activities;

            if (activities != null) {
                ArrayList<ActivityCache> activityCaches = new ArrayList<>();
                for (ActivityInfo activity : activities) {
                    Log.i(tag(), "activity className:" + activity.name);
                    activityCaches.add(new ActivityCache(p.getPluginName(), activity));
                }
                SlotManager.getInstance().setActivityInfo(activityCaches);
            }
            ServiceInfo[] services = packageInfo.services;
            if (services != null) {
                ArrayList<ServiceCache> serviceCaches = new ArrayList<>();
                for (ServiceInfo service : services) {
                    Log.i(tag(), "service className:" + service.name);
                    serviceCaches.add(new ServiceCache(p.getPluginName(), service));
                }
                SlotManager.getInstance().setServiceInfo(serviceCaches);
            }

            Class<?> pluginManagerClass = classLoader.loadClass("com.me.pluginlib.PluginManager");
            if (pluginManagerClass != null) {
                ReflectUtils.writeField(pluginManagerClass, null, "sClassLoader", classLoader);
                ReflectUtils.writeField(pluginManagerClass, null, "sResources", resources);
                ReflectUtils.writeField(pluginManagerClass, null, "sApplicationInfo", appInfo);
                Method setHost = pluginManagerClass.getDeclaredMethod("setHost", Object.class);
                setHost.invoke(null, Host.getInstance());
            }

            Class<?> appClass = null;

            Log.v("load app name", "::" + appInfo.name);
            if (!TextUtils.isEmpty(appInfo.name)) {

                appClass = getInstance().mClassLoader.get(p.getPluginName()).loadClass(appInfo.name);
                if (appClass != null) {
                    Method attachBaseContext = ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
                    attachBaseContext.setAccessible(true);
                    Application oApp = (Application) appClass.newInstance();
                    allContext.put(p.getPluginName(), oApp);
                    ReflectUtils.writeField(pluginManagerClass, null, "sApplicationContext", oApp);
                    ReflectUtils.writeField(pluginManagerClass, null, "oProviderService", ProviderService.getInstance(context));

                    Class<?> baseContext = classLoader.loadClass("com.me.pluginlib.PluginContext");
                    Context pluginBaseContext;
                    if (baseContext != null) {
                        pluginBaseContext = (Context) baseContext.getConstructor(Context.class).newInstance(context.getBaseContext());
                    } else {
                        pluginBaseContext = context.getBaseContext();
                    }
                    attachBaseContext.invoke(oApp, pluginBaseContext);

                    oApp.onCreate();
                }
            }
            ReceiverParser.installPluginReceiver(context, p.getPluginName(), packageInfo, resources);
            ManifestParser.installPluginInformation(context, p.getPluginName(), packageInfo, resources);

            HashMap<String, ProviderInfo> pInfo = new HashMap<>();
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (int i = 0; i < providers.length; i++) {
                    ProviderInfo provider = providers[i];
                    pInfo.put(provider.authority, provider);
                }
                ProviderService.updateInfo(pInfo);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | PackageManager.NameNotFoundException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void sendAppInfo(final String pluginName, final String dexPath, final String opt, final String libs) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                handlePackages((Application) initContext, new PackageArchiveData(pluginName, dexPath, opt, libs));
            }
        });
    }

    public Context getAppContext() {
        return initContext;
    }

    public Context getContext(String pluginName) {
        Context context = allContext.get(pluginName);
        return context == null ? sPlugins.initContext : context;
    }

    public HashMap<String, ClassLoader> allClassLoader() {
        return mClassLoader;
    }

    public void installClassLoader(String pluginName, ClassLoader classLoader) {
        mClassLoader.put(pluginName, classLoader);
    }

    public boolean isLoadPlugin() {
        return mLoadPlugin;
    }

    public void setLoadPlugin(boolean mLoadPlugin) {
        this.mLoadPlugin = mLoadPlugin;
    }

    public boolean isPatchClassLoader() {
        return mPatchClassLoader;
    }

    public void setPatchClassLoader(boolean patchClassLoader) {
        this.mPatchClassLoader = patchClassLoader;
    }

    Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        String className = SlotManager.getInstance().findClass(name);
        String pluginName = SlotManager.getInstance().findPluginName(className);
        if (!mClassLoader.isEmpty() && !TextUtils.isEmpty(pluginName)) {
            clazz = mClassLoader.get(pluginName).loadClass(className);
        }

        return clazz;
    }

    public ClassLoader getClassLoader(String pluginName) {
        ClassLoader classLoader = mClassLoader.get(pluginName);
        return classLoader == null ? Plugins.class.getClassLoader() : classLoader;
    }

    public void installAssetsOrLoad(Context c, String pluginName) {
        try {
            if (new File(Files.pluginDir(c, pluginName), pluginName).exists()) {
                new ParseThread(c, pluginName, ParseThread.OP_MOUNT_PLUGIN).start();
            } else {
                new ParseThread(c, pluginName, ParseThread.OP_PARSE_APK | ParseThread.OP_ASSETS | ParseThread.OP_MOUNT_PLUGIN).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void installContentOrLoad(Context c, String fileUrl) {
        try {
            Uri uri = Uri.parse(fileUrl);
            String apkName = uri.getLastPathSegment();

            if (new File(Files.pluginDir(c, apkName), apkName).exists()) {
                new ParseThread(c, uri, apkName, ParseThread.OP_MOUNT_PLUGIN).start();
            } else {
                new ParseThread(c, uri, apkName, ParseThread.OP_PARSE_APK | ParseThread.OP_ASSETS | ParseThread.OP_MOUNT_PLUGIN).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void installAssets(Context context, String assetsName) {
        try {
            new ParseThread(context, assetsName, ParseThread.OP_PARSE_APK | ParseThread.OP_ASSETS).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void installContentFile(Context context, String fileUri) {
        try {
            Uri uri = Uri.parse(fileUri);
            new ParseThread(context, uri, uri.getLastPathSegment(), ParseThread.OP_PARSE_APK | ParseThread.OP_CONTENT_FILE).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAssets(Context c, String pluginName) {
        try {
            new ParseThread(c, pluginName, ParseThread.OP_MOUNT_PLUGIN).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContent(Context c, String fileUri) {
        try {
            Uri uri = Uri.parse(fileUri);
            new ParseThread(c, uri, uri.getLastPathSegment(), ParseThread.OP_MOUNT_PLUGIN).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    String getProcessName() {
        int myPid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) initContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo proc : runningAppProcesses) {
            if (proc.pid == myPid) {
                return proc.processName;
            }
        }
        return "";
    }


    public void onApplicationCreateLocked() {
        Log.i(tag(), "onApplicationCreateLocked");
        File cmd = initContext.getFileStreamPath("load.cmd");
        if (!cmd.exists()) {
            try {
                InputStream open = initContext.getAssets().open("load.cmd");
                OutputStream out = new FileOutputStream(cmd);
                int available = open.available();
                byte[] buff = new byte[available];
                open.read(buff);
                out.write(buff);
                out.flush();
                open.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(cmd));
            String line = null;
            do {
                line = reader.readLine();
                if (line != null) {
                    this.cmd.add(line);
                    parseCommand(line);
                }
            } while (line != null);
            removeDeleteCommand();
            changeInstallCommand();
            genNextLoadCommand(cmd);
//            loadCurrent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * i:assets:// 加载assets
     * i:content://(fileprovider) 指定文件
     * r:name
     *
     * @param line
     */
    private void parseCommand(String line) {
        String[] commands = line.split("->");
        if (commands.length == 2) {
            String cmd = commands[0];
            String todo = commands[1];
            Uri uri = Uri.parse(todo);
            if (uri == null) return;
            String auth = uri.getAuthority();
            switch (cmd) {
                case "i":
                    if (uri.getScheme().equals("assets")) {
                        Plugins.getInstance().installAssetsOrLoad(initContext, auth);
                    } else if (uri.getScheme().equals("content")) {
                        Plugins.getInstance().installContentOrLoad(initContext, uri.toString());
                    }
                    break;
                case "r":
                    try {
                        Files.deleteAllFile(Files.pluginDir(initContext, todo));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "l":
                    if (uri.getScheme().equals("assets")) {
                        Plugins.getInstance().loadAssets(initContext, auth);
                    } else if (uri.getScheme().equals("content")) {
                        Plugins.getInstance().loadAssets(initContext, uri.getLastPathSegment());
                    }
                    break;
            }
        }
    }

    public void changeInstallCommand() {
        for (int i = 0; i < cmd.size(); i++) {
            String cc = cmd.get(i);
            if (cc.startsWith("i")) {
                cc = "l" + cc.substring(1);
//                changed.add(cc);
                cmd.set(i, cc);
            }
        }
    }

    public void removeDeleteCommand() {
        Iterator<String> iterator = cmd.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith("r")) {
                iterator.remove();
            }
        }
    }

    public void genNextLoadCommand(File cmd) {
        for (int i = 0; i < this.cmd.size(); i++) {
            if (this.cmd.get(i).contains("\r") || this.cmd.get(i).contains("\n")) {
                Log.e(tag(), "genNextLoadCommand: has \r or \n");
            }
        }

        try {
            if (cmd.delete() && cmd.createNewFile()) {
                PrintWriter pw = new PrintWriter(new FileWriter(cmd));
                for (int i = 0; i < this.cmd.size(); i++) {
                    pw.println(this.cmd.get(i));
                }
                pw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNewHandler() {
        Log.i(tag(), "setNewHandler");
        this.handler = new Handler();
    }

}