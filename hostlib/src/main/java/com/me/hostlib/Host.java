package com.me.hostlib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;

import com.me.hostlib.plugin.ActivityCache;
import com.me.hostlib.plugin.ActivitySlotManager;
import com.me.hostlib.plugin.ServiceCache;
import com.me.hostlib.plugin.ServiceSlotManager;
import com.me.hostlib.plugin.SlotManager;

import java.util.ArrayList;

public class Host {

    private static Host instance;

    public static Host getInstance() {
        if (instance == null) {
            synchronized (Host.class) {
                if (instance == null)
                    instance = new Host();
            }
        }
        return instance;
    }

    private Host() {
    }

    public boolean prepareStartService(Intent intent) {
        ServiceInfo serviceInfo = getService(intent);
        if (serviceInfo != null) {
            String key = ServiceSlotManager.getInstance().dispatchSlot(serviceInfo);
            intent.setComponent(new ComponentName(intent.getComponent().getPackageName(), key));
            intent.putExtra("ServiceInfo", serviceInfo);
        }
        return true;
    }

    public boolean prepareStopService(Intent intent) {
        ServiceInfo serviceInfo = getService(intent);
        if (serviceInfo != null) {
            String key = ServiceSlotManager.getInstance().dispatchSlot(serviceInfo);
            intent.setComponent(new ComponentName(intent.getComponent().getPackageName(), key));
            intent.putExtra("ServiceInfo", serviceInfo);

        }
        return true;
    }

    public boolean prepareStartActivity(Intent intent) {
        ActivityInfo activity = getActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (activity != null) {
            String key = ActivitySlotManager.getInstance().dispatchSlot(activity);
            intent.setComponent(new ComponentName(intent.getComponent().getPackageName(), key));
            intent.putExtra("ActivityInfo", activity);
        }
        return true;
    }


    public boolean startActivityForResult(Activity target, Intent intent, int request, Bundle bundle) {
        prepareStartActivity(intent);
        target.startActivityForResult(intent, request, bundle);
        return false;
    }

    public boolean startActivityForResult(Activity target, Intent intent, int request) {
        return startActivityForResult(target, intent, request, null);
    }

    public boolean startActivity(Activity target, Intent intent, Bundle bundle) {
        prepareStartActivity(intent);
        target.startActivity(intent, bundle);
        return true;
    }

    public boolean startActivity(Activity target, Intent intent) {
        return startActivity(target, intent, null);
    }

    private ActivityInfo getActivity(Intent intent) {
        String className = intent.getComponent().getClassName();
        ArrayList<ActivityCache> activityInfo = SlotManager.getInstance().getActivityInfo();
        for (ActivityCache a : activityInfo) {
            if (a.getAi().name.equals(className)) {
                return a.getAi();
            }
        }
        return null;
    }


    private ServiceInfo getService(Intent intent) {
        String className = intent.getComponent().getClassName();
        ArrayList<ServiceCache> serviceInfo = SlotManager.getInstance().getServiceInfo();
        for (ServiceCache a : serviceInfo) {
            if (a.getAi().name.equals(className)) {
                return a.getAi();
            }
        }
        return null;
    }

    public boolean startService(Context context, Intent intent) {
        prepareStartService(intent);
        context.startService(intent);
        return false;
    }

    public boolean stopService(Context context, Intent intent) {
        prepareStopService(intent);
        context.stopService(intent);
        return false;
    }

    public void cleanServiceSlot(String name) {
        ServiceSlotManager.getInstance().cleanServiceSlot(name);
    }

}

