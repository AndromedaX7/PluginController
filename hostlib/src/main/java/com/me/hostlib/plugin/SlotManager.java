package com.me.hostlib.plugin;

import java.util.ArrayList;

public class SlotManager {
    private static volatile SlotManager instance;
    private final ArrayList<ActivityCache> activityInfo = new ArrayList<>();
    private final ArrayList<ServiceCache> serviceInfo = new ArrayList<>();

    private SlotManager() {
    }

    public static SlotManager getInstance() {
        if (instance == null) {
            synchronized (SlotManager.class) {
                if (instance == null) {
                    instance = new SlotManager();
                }
            }

        }
        return instance;
    }

    public ArrayList<ActivityCache> getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(ArrayList<ActivityCache> activityInfo) {
        this.activityInfo.addAll(activityInfo);
    }

    public ArrayList<ServiceCache> getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ArrayList<ServiceCache> serviceInfo) {
        this.serviceInfo.addAll(serviceInfo);
    }

    public String findClass(String name) {
        return SlotController.getInstance().findSlotClass(name);
    }

    public String findPluginName(String className) {
        for (ActivityCache a : activityInfo) {
            if (a.getAi().name.equals(className)) {
                return a.getPluginName();
            }
        }

        for (ServiceCache a : serviceInfo) {
            if (a.getAi().name.equals(className)) {
                return a.getPluginName();
            }
        }
        return null;
    }
}
