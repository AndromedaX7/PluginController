package com.me.hostlib.plugin;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.me.hostlib.Plugins;
import com.me.hostlib.ProcessMapping;

import java.util.HashMap;
import java.util.Set;

public class ActivitySlotManager {
    private static final Uri uri = Uri.parse("content://" + Plugins.getInstance().getAppContext().getPackageName() + ".mapping");
    static HashMap<String, String> sHostActivityMap = new HashMap<>();
    private static String sPackageName;
    private static volatile ActivitySlotManager instance;
    private ContentProviderClient client;

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

    public static void setPackageName(String sPackageName) {
        ActivitySlotManager.sPackageName = sPackageName;
    }

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
        String name = activityInfo.applicationInfo.name;
        String className = activityInfo.applicationInfo.className;
        String processName = activityInfo.processName;

        String prefix = "";
        Log.e("name", name + "");
        Log.e("className", className + "");
        Log.e("processName", processName + "");
        if (!TextUtils.isEmpty(processName)) {
            if (client == null)
                client = Plugins.getInstance().getAppContext().getContentResolver().acquireContentProviderClient(uri);
            try {
                if (client != null) {
                    Bundle call = client.call(ProcessMapping.mappingProcess, processName, null);
                    prefix = call.getString(ProcessMapping.mappingProcess);
                    ContentValues values = new ContentValues();
                    values.put(ProcessMapping.internal, prefix);
                    values.put(ProcessMapping.from, processName);
                    client.insert(uri, values);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Set<String> strings = sHostActivityMap.keySet();
        for (String key : strings) {
            if (sHostActivityMap.get(key + prefix) == null) {
                sHostActivityMap.put(key + prefix, activityInfo.name);
                return key + prefix;
            }
        }
        return "";
    }
}
