package com.me.pluginlib;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PluginManager {
    public static Resources sResources;
    public static ApplicationInfo sApplicationInfo;
    public static ClassLoader sClassLoader;
    public static Object oHost;


    public static void setHost(Object host) {
        oHost = host;
    }

    public static boolean prepareStartActivity(Intent intent) {
        try {
            Method startActivityForResult = oHost.getClass().getDeclaredMethod("prepareStartActivity", Intent.class);
            startActivityForResult.invoke(oHost, intent);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    static boolean prepareStartService(Intent intent) {
        try {
            Method startActivityForResult = oHost.getClass().getDeclaredMethod("prepareStartService", Intent.class);
            startActivityForResult.invoke(oHost, intent);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    static boolean prepareStopService(Intent intent) {
        try {
            Method startActivityForResult = oHost.getClass().getDeclaredMethod("prepareStopService", Intent.class);
            startActivityForResult.invoke(oHost, intent);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean prepareBindService(Intent intent) {
        try {
            Method startActivityForResult = oHost.getClass().getDeclaredMethod("prepareBindService", Intent.class);
            startActivityForResult.invoke(oHost, intent);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static boolean prepareUnbindService(Intent intent) {
//        try {
//            Method startActivityForResult = oHost.getClass().getDeclaredMethod("prepareUnbindService", Intent.class);
//            startActivityForResult.invoke(oHost, intent);
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    public static void cleanServiceSlot(String name) {
        try {
            Method cleanServiceSlot = oHost.getClass().getDeclaredMethod("cleanServiceSlot", String.class);
            cleanServiceSlot.invoke(oHost, name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
