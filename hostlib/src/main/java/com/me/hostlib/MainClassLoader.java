package com.me.hostlib;

import android.util.Log;

import com.me.hostlib.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

public class MainClassLoader extends PathClassLoader {

    private final static String TAG = "MainClassLoader";
    private final ClassLoader mOrig;

    private Method findResourceMethod;

    private Method findResourcesMethod;

    private Method findLibraryMethod;

    private Method getPackageMethod;

    public MainClassLoader(ClassLoader parent, ClassLoader orig) {
        super("", "", parent);
        mOrig = orig;
        copyFromOriginal(orig);
        initMethods(orig);
    }

    private void copyFromOriginal(ClassLoader orig) {
        copyFieldValue("pathList", orig);
    }

    private void copyFieldValue(String field, ClassLoader orig) {
        try {
            Field f = ReflectUtils.getField(orig.getClass(), field);
            if (f == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "ClassLoader copyFieldValue" + f);
                }
                return;
            }

            ReflectUtils.removeFieldFinalModifier(f);


            Object o = ReflectUtils.readField(f, orig);
            ReflectUtils.writeField(f, this, o);

            if (BuildConfig.DEBUG) {
                Object test = ReflectUtils.readField(f, this);
                Log.d(TAG, "copyFieldValue: Copied. f=" + field + "; actually=" + test + "; orig=" + o);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initMethods(ClassLoader cl) {
        Class<?> c = cl.getClass();
        findResourceMethod = ReflectUtils.getMethod(c, "findResource", String.class);
        findResourceMethod.setAccessible(true);
        findResourcesMethod = ReflectUtils.getMethod(c, "findResources", String.class);
        findResourcesMethod.setAccessible(true);
        findLibraryMethod = ReflectUtils.getMethod(c, "findLibrary", String.class);
        findLibraryMethod.setAccessible(true);
        getPackageMethod = ReflectUtils.getMethod(c, "getPackage", String.class);
        getPackageMethod.setAccessible(true);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Log.i(TAG,"load class:"+ name);
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> c = null;

        if ("com.me.hostlib.ProcessMapping".equals(className)) {
            c = mOrig.loadClass(className);
        }
        if (c == null)
            c = Plugins.getInstance().loadClass(className, resolve);
        if (c != null) {
            return c;
        }

        try {
            c = mOrig.loadClass(className);
            if (c != null) {
                return c;
            }
        } catch (Throwable e) {
            Log.w(TAG,"can not load class:"+className);
        }

        return super.loadClass(className, resolve);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        return super.findClass(className);
    }

    @Override
    protected URL findResource(String resName) {
        try {
            return (URL) findResourceMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.findResource(resName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Enumeration<URL> findResources(String resName) {
        try {
            return (Enumeration<URL>) findResourcesMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.findResources(resName);
    }

    @Override
    public String findLibrary(String libName) {
        try {
            return (String) findLibraryMethod.invoke(mOrig, libName);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.findLibrary(libName);
    }

    @Override
    protected Package getPackage(String name) {
        // 金立手机的某些ROM(F103,F103L,F303,M3)代码ClassLoader.getPackage去掉了关键的保护和错误处理(2015.11~2015.12左右)，会返回null
        // 悬浮窗某些draw代码触发getPackage(...).getName()，getName出现空指针解引，导致悬浮窗进程出现了大量崩溃
        // 此处实现和AOSP一致确保不会返回null
        // SONGZHAOCHUN, 2016/02/29
        if (name != null && !name.isEmpty()) {
            Package pack = null;
            try {
                pack = (Package) getPackageMethod.invoke(mOrig, name);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (pack == null) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "NRH lcl.gp.1: n=" + name);
                }
                pack = super.getPackage(name);
            }
            if (pack == null) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "NRH lcl.gp.2: n=" + name);
                }
                return definePackage(name, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", null);
            }
            return pack;
        }
        return null;
    }
}
