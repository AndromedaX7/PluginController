package com.me.hostlib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.me.hostlib.plugin.ActivityCache;
import com.me.hostlib.plugin.ActivitySlotManager;
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
    private static Handler handler;
    private static final String TAG = "Plugins";
    private static volatile Plugins sPlugins;
    private HashMap<String, ClassLoader> mClassLoader = new HashMap<>();

    public void installClassLoader(String pluginName, ClassLoader classLoader) {
        mClassLoader.put(pluginName, classLoader);
    }

    private boolean mPatchClassLoader = false;
    private boolean mLoadPlugin = false;

    public void setPatchClassLoader(boolean patchClassLoader) {
        this.mPatchClassLoader = patchClassLoader;
    }

    public void setLoadPlugin(boolean mLoadPlugin) {
        this.mLoadPlugin = mLoadPlugin;
    }

    public boolean isLoadPlugin() {
        return mLoadPlugin;
    }

    public boolean isPatchClassLoader() {
        return mPatchClassLoader;
    }

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

    private static void handlePackages(Context context, Message msg) {
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
            ArrayList<ActivityCache> activityCaches = new ArrayList<>();
            for (ActivityInfo activity : activities) {
                Log.e("activity className", activity.name);
                activityCaches.add(new ActivityCache(p.getPluginName(), activity));
            }
            SlotManager.getInstance().setActivityInfo(activityCaches);

            ServiceInfo[] services = packageInfo.services;
            ArrayList<ServiceCache> serviceCaches = new ArrayList<>();
            for (ServiceInfo service : services) {
                Log.e("service className", service.name);
                serviceCaches.add(new ServiceCache(p.getPluginName(), service));
            }
            SlotManager.getInstance().setServiceInfo(serviceCaches);


            Class<?> pluginManagerClass = classLoader.loadClass("com.me.pluginlib.PluginManager");
            if (pluginManagerClass != null) {
                ReflectUtils.writeField(pluginManagerClass, null, "sClassLoader", classLoader);
                ReflectUtils.writeField(pluginManagerClass, null, "sResources", resources);
                ReflectUtils.writeField(pluginManagerClass, null, "sApplicationInfo", appInfo);
                Method setHost = pluginManagerClass.getDeclaredMethod("setHost", Object.class);
                setHost.invoke(null, Host.getInstance());
            }


            Class<?> aClass = null;

            Log.e("appName", ";;" + appInfo.name);
            if (!TextUtils.isEmpty(appInfo.name)) {
                aClass = getInstance().mClassLoader.get(p.getPluginName()).loadClass(appInfo.name);
                if (aClass != null) {
                    Application oApp = (Application) aClass.newInstance();

                    oApp.onCreate();
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | PackageManager.NameNotFoundException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
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


    public void installOrLoad(Context c, String pluginName) {
        try {
            if (new File(Files.pluginDir(c, pluginName), pluginName).exists()) {
                new ParseThread(c, pluginName, ParseThread.OP_PARSE_APK).start();
            } else {
                new ParseThread(c, pluginName, ParseThread.OP_PARSE_APK | ParseThread.OP_ASSETS).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendAppInfo(String pluginName, String dexPath, String opt, String libs) {
        Message msg = new Message();
        msg.what = PLUGIN_INSTALL;
        msg.obj = new PackageArchiveData(pluginName, dexPath, opt, libs);
        handler.sendMessage(msg);
    }

    public static final int PLUGIN_INSTALL = 65534;
}
