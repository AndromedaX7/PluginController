package com.me.hostlib.plugin;

import android.content.pm.ServiceInfo;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Set;

public class ServiceSlotManager {
    private static volatile ServiceSlotManager instance;

    public static ServiceSlotManager getInstance() {
        if (instance == null) {
            synchronized (ServiceSlotManager.class) {
                if (instance == null) {
                    instance = new ServiceSlotManager();
                }
            }
        }
        return instance;
    }

    private ServiceSlotManager() {

    }




    public String findServiceClass(String slotName) {
//        if (!sPluginActivityMap.isEmpty()) {
//            ActivityInfo activityInfo = sPluginActivityMap.get(slotName);
//            if (activityInfo != null) {
//                return activityInfo.name;
//            }
//        }
        String className = sHostServiceMap.get(slotName);
        if (TextUtils.isEmpty(className)) {
            return slotName;
        } else {
            return className;
        }
    }

    public String dispatchSlot(ServiceInfo activityInfo) {
        for (String key : sHostServiceMap.keySet()) {
            if (sHostServiceMap.get(key) != null && sHostServiceMap.get(key).equals(activityInfo.name)) {
                return key;
            }
        }
        Set<String> strings = sHostServiceMap.keySet();
        for (String key : strings) {
            if (sHostServiceMap.get(key) == null) {
                sHostServiceMap.put(key, activityInfo.name);
                return key;
            }
        }
        return "";
    }

    public void cleanServiceSlot(String name) {
        for (String key : sHostServiceMap.keySet()) {
            if (sHostServiceMap.get(key) != null && sHostServiceMap.get(key).equals(name)) {
                sHostServiceMap.put(key, null);
                return;
            }
        }
    }
}
