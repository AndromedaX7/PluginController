package com.me.hostlib.plugin;

import java.util.ArrayList;

public class SlotManager {
    private static volatile SlotManager instance;

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

    private final ArrayList<ActivityCache> activityInfo = new ArrayList<>();
    private final ArrayList<ServiceCache> serviceInfo = new ArrayList<>();

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

    private SlotManager() {
    }


    public String findClass(String name) {
//        String className = null;
//        className = ActivitySlotManager.getInstance().findActivityClass(name);
//        if (TextUtils.isEmpty(className)) {
//            className = ServiceSlotManager.getInstance().findServiceClass(name);
//        }
//        return className;

        String className = null;
        className = ActivitySlotManager.getInstance().findActivityClass(name);
        if (className.equals(name)) {
            className = ServiceSlotManager.getInstance().findServiceClass(name);
        }
        return className;
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
