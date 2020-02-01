package com.me.hostlib;

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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.me.hostlib.plugin.ActivityCache;
import com.me.hostlib.plugin.ActivitySlotManager;
import com.me.hostlib.plugin.ManifestParser;
import com.me.hostlib.plugin.ProviderService;
import com.me.hostlib.plugin.ReceiverParser;
import com.me.hostlib.plugin.ServiceCache;
import com.me.hostlib.plugin.ServiceSlotManager;
import com.me.hostlib.plugin.SlotManager;
import com.me.hostlib.utils.PackageArchiveData;
import com.me.hostlib.utils.PatchClassLoaderUtils;
import com.me.hostlib.utils.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class Plugins {
    public static final int PLUGIN_INSTALL = 65534;
    private static final String TAG = "Plugins";
    private static Handler handler;
    private static volatile Plugins sPlugins;
    private static HashMap<String, Context> allContext = new HashMap<>();
    private HashMap<String, ClassLoader> mClassLoader = new HashMap<>();
    private boolean mPatchClassLoader = false;
    private boolean mLoadPlugin = false;
    private Context initContext;

    private Plugins() {
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
        ActivitySlotManager.setPackageName(application.getPackageName());
        ServiceSlotManager.setPackageName(application.getPackageName());
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case PLUGIN_INSTALL:
                        handlePackages(application, msg);
                        break;
                }
                return true;
            }
        });
        Plugins plugins = getInstance();
        if (!sPlugins.isPatchClassLoader()) {
            boolean b = PatchClassLoaderUtils.patch(application);
            plugins.setPatchClassLoader(b);
            if (b) {
                Log.i(TAG, "Patch ClassLoader success");
            } else {
                Log.i(TAG, "Patch ClassLoader false");
            }
        }


    }

    private static void handlePackages(Application context, Message msg) {
        try {
            PackageArchiveData p = (PackageArchiveData) msg.obj;
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
                    Log.e("activity className", activity.name);
                    activityCaches.add(new ActivityCache(p.getPluginName(), activity));
                }
                SlotManager.getInstance().setActivityInfo(activityCaches);
            }
            ServiceInfo[] services = packageInfo.services;
            if (services != null) {
                ArrayList<ServiceCache> serviceCaches = new ArrayList<>();
                for (ServiceInfo service : services) {
                    Log.e("service className", service.name);
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

            Log.e("appName", "::" + appInfo.name);
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
            ManifestParser.installPluginReceiver(context, p.getPluginName(), packageInfo, resources);

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

    public static void sendAppInfo(String pluginName, String dexPath, String opt, String libs) {
        Message msg = new Message();
        msg.what = PLUGIN_INSTALL;
        msg.obj = new PackageArchiveData(pluginName, dexPath, opt, libs);
        handler.sendMessage(msg);
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

    public void installOrLoad(Context c, String pluginName) {
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
}