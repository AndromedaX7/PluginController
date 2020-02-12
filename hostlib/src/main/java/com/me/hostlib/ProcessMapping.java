package com.me.hostlib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Set;

public class ProcessMapping extends ContentProvider {

    public static final String mappingProcess = "mappingProcess";
    public static final String clearActivitySlot = "clearActivitySlot";
    public static final String clearServiceSlot = "clearServiceSlot";
    public static final String findRealClassName = "findRealClassName";
    public static final String mappingActivity = "mappingActivity";
    public static final String mappingService = "mappingService";
    private HashMap<String, String> mapping = new HashMap<>();
    private HashMap<String, String> runAppMapping = new HashMap<>();
    private HashMap<String, String> hostActivityMap = new HashMap<>();
    private HashMap<String, String> hostServiceMap = new HashMap<>();

    public ProcessMapping() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        Plugins.getInstance().setNewHandler();
        Plugins.getInstance().onApplicationCreateLocked();
        String packageName = getContext().getPackageName();

        Log.e("Plugin hash", Plugins.getInstance().hashCode() + "--");
        if (false) {
            for (int i = 1; i <= 10; i++) {
                mapping.put("p" + i, "");
            }
            for (int i = 1; i <= 10; i++) {
                runAppMapping.put("app" + i, "");
            }
        }
        for (int i = 1; i <= 10; i++) {
            hostActivityMap.put(packageName + ".ActivityS" + i, null);
        }
        for (int i = 1; i <= 10; i++) {
            hostServiceMap.put(packageName + ".ServiceS" + i, null);
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle bundle = new Bundle();
        switch (method) {
            case mappingProcess:
                bundle.putString(mappingProcess, findProcessMapping(arg));
                return bundle;

            case mappingActivity:
                if (extras == null) return null;
                ActivityInfo activityInfo = extras.getParcelable(mappingActivity);
                if (activityInfo == null) return null;
                bundle.putString(mappingActivity, mappingActivity(activityInfo));
                return bundle;
            case mappingService:
                if (extras == null) return null;
                ServiceInfo serviceInfo = extras.getParcelable(mappingService);
                if (serviceInfo == null) return null;
                bundle.putString(mappingService, mappingService(serviceInfo));
                return bundle;

            case findRealClassName:
                bundle.putString(findRealClassName, findRealClassName(arg));
                return bundle;

            case clearActivitySlot:
                if (arg != null) {
                    if (arg.endsWith("p1") || arg.endsWith("p2") || arg.endsWith("p3") || arg.endsWith("p4") || arg.endsWith("p5") ||
                            arg.endsWith("p6") || arg.endsWith("p7") || arg.endsWith("p8") || arg.endsWith("p9") || arg.endsWith("p10")) {
                        if (hostActivityMap.get(arg) != null) {
                            hostActivityMap.remove(arg);
                        }
                    } else {
                        if (hostActivityMap.get(arg) != null) {
                            hostActivityMap.put(arg, null);
                        }
                    }
                }
                break;

            case clearServiceSlot:
                for (String key2 : hostServiceMap.keySet()) {
                    if (hostServiceMap.get(key2) != null && hostServiceMap.get(key2).equals(arg)) {
                        if (key2.endsWith("p1") || key2.endsWith("p2") || key2.endsWith("p3") || key2.endsWith("p4") || key2.endsWith("p5") ||
                                key2.endsWith("p6") || key2.endsWith("p7") || key2.endsWith("p8") || key2.endsWith("p9") || key2.endsWith("p10")) {
                            hostServiceMap.remove(key2);
                        } else {
                            hostServiceMap.put(arg, null);
                        }
                        return bundle;
                    }
                }
                break;
        }
        return bundle;
    }

    private String findProcessMapping(String from) {
        for (String m : mapping.keySet()) {
            if (mapping.get(m).equals(from)) {
                return m;
            }
        }

        for (String m : mapping.keySet()) {
            if (TextUtils.isEmpty(mapping.get(m))) {
                return m;
            }
        }
        return "";

    }

    private String mappingActivity(ActivityInfo activityInfo) {
        String processName = activityInfo.processName;
        String prefix = "";
        if (false) {
            if (processName != null) {
                prefix = findProcessMapping(processName);
                if (TextUtils.isEmpty(mapping.get(prefix))) {
                    Uri uri = Uri.parse("content://" + getContext().getPackageName() + "." + prefix);
                    getContext().getContentResolver().query(uri, null, null, null, null);
                    mapping.put(prefix, processName);
                }
            }
        }
        Set<String> strings = hostActivityMap.keySet();
        for (String key : strings) {
            if (key.endsWith("p1") || key.endsWith("p2") || key.endsWith("p3") || key.endsWith("p4") || key.endsWith("p5") ||
                    key.endsWith("p6") || key.endsWith("p7") || key.endsWith("p8") || key.endsWith("p9") || key.endsWith("p10")) {
                continue;
            }
            if (hostActivityMap.get(key + prefix) == null) {
                hostActivityMap.put(key + prefix, activityInfo.name);
                return key + prefix;
            }
        }

        return "";
    }

    private String mappingService(ServiceInfo serviceInfo) {
        String processName = serviceInfo.processName;
        String prefix = "";
        if (false) {
            if (!TextUtils.isEmpty(processName)) {
                prefix = findProcessMapping(processName);
                if (TextUtils.isEmpty(mapping.get(prefix))) {
                    Uri uri = Uri.parse("content://" + getContext().getPackageName() + "." + prefix);
                    getContext().getContentResolver().query(uri, null, null, null, null);
                    mapping.put(prefix, processName);
                }
            }
        }

        Set<String> strings = hostServiceMap.keySet();
        for (String key : strings) {
            if (key.endsWith("p1") || key.endsWith("p2") || key.endsWith("p3") || key.endsWith("p4") || key.endsWith("p5") ||
                    key.endsWith("p6") || key.endsWith("p7") || key.endsWith("p8") || key.endsWith("p9") || key.endsWith("p10")) {
                continue;
            }
            String className = hostServiceMap.get(key + prefix);
            if (className != null) {
                if (className.equals(serviceInfo.name))
                    return key + prefix;
            }
        }
        for (String key : strings) {
            if (key.endsWith("p1") || key.endsWith("p2") || key.endsWith("p3") || key.endsWith("p4") || key.endsWith("p5") ||
                    key.endsWith("p6") || key.endsWith("p7") || key.endsWith("p8") || key.endsWith("p9") || key.endsWith("p10")) {
                continue;
            }
            if (hostServiceMap.get(key + prefix) == null) {
                hostServiceMap.put(key + prefix, serviceInfo.name);
                return key + prefix;
            }
        }

        return "";
    }

    private String findRealClassName(String key) {
        String real = hostActivityMap.get(key);
        if (TextUtils.isEmpty(real)) {
            real = hostServiceMap.get(key);
        }
        if (TextUtils.isEmpty(real)) return key;
        else return real;
    }
}
