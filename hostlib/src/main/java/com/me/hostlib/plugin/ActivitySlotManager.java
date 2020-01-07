package com.me.hostlib.plugin;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Set;

public class ActivitySlotManager {
    static HashMap<String, String> sHostActivityMap = new HashMap<>();
    private static String sPackageName;

    public static void setPackageName(String sPackageName) {
        ActivitySlotManager.sPackageName = sPackageName;
    }

    private ActivitySlotManager() {
        sHostActivityMap.put(sPackageName + ".ActivityS1", null);
        sHostActivityMap.put(sPackageName + ".ActivityS2", null);
        sHostActivityMap.put(sPackageName + ".ActivityS3", null);
        sHostActivityMap.put(sPackageName + ".ActivityS4", null);
        sHostActivityMap.put(sPackageName + ".ActivityS5", null);
        sHostActivityMap.put(sPackageName + ".ActivityS6", null);
        sHostActivityMap.put(sPackageName + ".ActivityS7", null);
        sHostActivityMap.put(sPackageName + ".ActivityS8", null);
        sHostActivityMap.put(sPackageName + ".ActivityS9", null);
        sHostActivityMap.put(sPackageName + ".ActivityS10", null);
    }

    private static volatile ActivitySlotManager instance;

    public static ActivitySlotManager getInstance() {
        if (instance == null) {
            synchronized (ActivitySlotManager.class) {
                if (instance == null)
                    instance = new ActivitySlotManager();
            }
        }
        return instance;
    }


    public String findActivityClass(String slotName) {
//        if (!sPluginActivityMap.isEmpty()) {
//            ActivityInfo activityInfo = sPluginActivityMap.get(slotName);
//            if (activityInfo != null) {
//                return activityInfo.name;
//            }
//        }
        String className = sHostActivityMap.get(slotName);
        if (TextUtils.isEmpty(className)) {
            return slotName;
        } else {
            sHostActivityMap.put(slotName, null);
            return className;
        }
    }

    public String dispatchSlot(ActivityInfo activityInfo) {
        Set<String> strings = sHostActivityMap.keySet();
        for (String key : strings) {
            if (sHostActivityMap.get(key) == null) {
                sHostActivityMap.put(key, activityInfo.name);
                return key;
            }
        }
        return "";
    }
}
