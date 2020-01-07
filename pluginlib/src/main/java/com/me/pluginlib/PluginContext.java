package com.me.pluginlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PluginContext extends ContextThemeWrapper {
    private Resources mResources;
    private ApplicationInfo mApplicationInfo;
    private ClassLoader mClassLoader;
    private LayoutInflater mInflater;

    public PluginContext(Context context) {
        super(context, android.R.style.Theme);
        this.mResources = PluginManager.sResources;
        this.mApplicationInfo = PluginManager.sApplicationInfo;
        this.mClassLoader = PluginManager.sClassLoader;

    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name) && mClassLoader != null) {
            LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
            mInflater = inflater.cloneInContext(this);
            mInflater.setFactory(mFactory);
            return mInflater;
        }
        return super.getSystemService(name);
    }

    LayoutInflater.Factory mFactory = new LayoutInflater.Factory() {

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return handleCreateView(name, context, attrs);
        }
    };

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }

    @Override
    public AssetManager getAssets() {
        if (mResources != null) {
            return mResources.getAssets();
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (mResources != null) {
            return mResources;
        }
        return super.getResources();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (mApplicationInfo != null) {
            return mApplicationInfo;
        }
        return super.getApplicationInfo();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mClassLoader != null) {
            return mClassLoader;
        }
        return super.getClassLoader();
    }

    private final View handleCreateView(String name, Context context, AttributeSet attrs) {
        View v = null;
        try {
            Class c = mClassLoader.loadClass(name);
            do {
                if (c == null) {
                    // 没找到，不管
                    break;
                }
                if (c == ViewStub.class) {
                    // 系统特殊类，不管
                    break;
                }
                if (c.getClassLoader() != mClassLoader) {
                    // 不是插件类，不管
                    break;
                }
                Constructor<?> construct = c.getConstructor(Context.class, AttributeSet.class);
                v = (View) construct.newInstance(context, attrs);
            } while (false);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//            e.printStackTrace();
        }
//        DLog.i("handleCreateView name: " + name + " v: " + v);
        return v;
    }


    @Override
    public void startActivity(Intent intent) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivity(intent);
        } else
            super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivity(intent, options);
        } else
            super.startActivity(intent, options);
    }


    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (mClassLoader != null) {
            PluginManager.prepareBindService(service);
            super.bindService(service, conn, flags);
        }
        return super.bindService(service, conn, flags);
    }

    @Override
    public ComponentName startService(Intent service) {
        if (mClassLoader != null) {
            PluginManager.prepareStartService(service);
        }
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        if (mClassLoader != null) {
            PluginManager.prepareStopService(name);
        }
        return super.stopService(name);
    }
}
