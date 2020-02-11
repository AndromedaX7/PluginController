package com.me.hostlib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;

import com.me.hostlib.plugin.ActivityCache;
import com.me.hostlib.plugin.ActivitySlotManager;
import com.me.hostlib.plugin.AndroidManifest;
import com.me.hostlib.plugin.ManifestParser;
import com.me.hostlib.plugin.ServiceCache;
import com.me.hostlib.plugin.ServiceSlotManager;
import com.me.hostlib.plugin.SlotManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Host {

    private static Host instance;

    private Host() {
    }

    public static Host getInstance() {
        if (instance == null) {
            synchronized (Host.class) {
                if (instance == null)
                    instance = new Host();
            }
        }
        return instance;
    }

    private void prepareService(Intent intent) {
        ServiceInfo serviceInfo = getService(intent);
        if (serviceInfo != null) {
            String key = ServiceSlotManager.getInstance().dispatchSlot(serviceInfo);
            intent.setComponent(new ComponentName(intent.getComponent().getPackageName(), key));
            intent.putExtra("ServiceInfo", serviceInfo);
        }
    }

    public boolean prepareStartService(Intent intent) {
        prepareService(intent);
        return true;
    }

    public boolean prepareStopService(Intent intent) {
        prepareService(intent);
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
        ComponentName component = intent.getComponent();
        if (component != null) {
            String className = component.getClassName();
            ArrayList<ActivityCache> activityInfo = SlotManager.getInstance().getActivityInfo();
            for (ActivityCache a : activityInfo) {
                if (a.getAi().name.equals(className)) {
                    return a.getAi();
                }
            }
        } else {
            String action = intent.getAction();
            if (action != null) {
                HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> components = ManifestParser.getInstance().getComponents();
                if (components == null) return null;
                HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> byAction = new HashMap<>(ManifestParser.getInstance().findByAction(action, components));
                if (intent.getCategories() != null && !intent.getCategories().isEmpty()) {
                    byAction = ManifestParser.getInstance().findByCategory(intent.getCategories(), byAction);
                }
                if (byAction.isEmpty()) {
                    return null;
                } else {
                    AndroidManifest.Component cc = null;
                    for (AndroidManifest.IntentFilter i : byAction.keySet()) {
                        cc = byAction.get(i);
                        break;
                    }
                    if (cc != null) {

                        Log.w("receive action", action  );
                        if (intent.getCategories()!=null)
                        Log.w("receive category ","::"+intent.getCategories().toString());
                        String name = cc.getName();
                        ComponentInfo componentInfo = ManifestParser.getInstance().getInfoCache().get(name);
                        if (componentInfo == null) return null;
                        if (cc.type() == AndroidManifest.ComponentType.Activity) {
                            Set<String> categories = intent.setAction(null).getCategories();
                            if (categories != null) {
                                categories.clear();
                            }
                            intent.setComponent(new ComponentName(Plugins.getInstance().getAppContext(), componentInfo.name));

                            return (ActivityInfo) componentInfo;
                        } else return null;
                    }
                }

            }

        }
        return null;
    }


    private ServiceInfo getService(Intent intent) {
        String className = intent.getComponent().getClassName();
        if (className!=null) {
            ArrayList<ServiceCache> serviceInfo = SlotManager.getInstance().getServiceInfo();
            for (ServiceCache a : serviceInfo) {
                if (a.getAi().name.equals(className)) {
                    return a.getAi();
                }
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

    public boolean bindService(Context c, Intent intent, ServiceConnection conn, int flag) {
        prepareService(intent);
        c.bindService(intent, conn, flag);
        return true;
    }

    public boolean unbindService(Context c, ServiceConnection conn) {
        c.unbindService(conn);
        return true;
    }

    public void cleanServiceSlot(String name) {
        ServiceSlotManager.getInstance().cleanServiceSlot(name);
    }

    public boolean prepareBindService(Intent intent) {
        prepareService(intent);
        return true;
    }

    public boolean prepareUnbindService(Intent intent) {
//        prepareService(intent);
        return true;
    }
}

