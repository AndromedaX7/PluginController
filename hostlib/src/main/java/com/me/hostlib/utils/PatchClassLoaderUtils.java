package com.me.hostlib.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.me.hostlib.BuildConfig;
import com.me.hostlib.MainClassLoader;

public class PatchClassLoaderUtils {

    private static final String TAG = "PatchClassLoaderUtils";

    public static boolean patch(Application application) {
        try {
            Context oBase = application.getBaseContext();
            if (oBase == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pclu.p: nf mb. ap cl=" + application.getClass());
                }
                return false;

            }

            Object oPackageInfo = ReflectUtils.readField(oBase, "mPackageInfo");
            if (oPackageInfo == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pclu.p: nf mpi. mb cl=" + oBase.getClass());
                }
                return false;
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "patch: mBase cl=" + oBase.getClass() + "; mPackageInfo cl=" + oPackageInfo.getClass());
            }

            ClassLoader oClassLoader = (ClassLoader) ReflectUtils.readField(oPackageInfo, "mClassLoader");
            if (oClassLoader == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "pclu.p: nf mpi. mb cl=" + oBase.getClass() + "; mpi cl=" + oPackageInfo.getClass());
                }
                return false;
            }
            ClassLoader cl = new MainClassLoader(oClassLoader.getParent(), oClassLoader);
            ReflectUtils.writeField(oPackageInfo, "mClassLoader", cl);

            // 设置线程上下文中的ClassLoader为RePluginClassLoader
            // 防止在个别Java库用到了Thread.currentThread().getContextClassLoader()时，“用了原来的PathClassLoader”，或为空指针
            Thread.currentThread().setContextClassLoader(cl);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "patch: patch mClassLoader ok");
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return true;
    }
}
