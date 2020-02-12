package com.me.hostlib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
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
    public static final String clearSlot = "clearSlot";
    public static final String findRealClassName = "findRealClassName";
    public static final String mappingActivity = "mappingActivity";
    private String packageName;
    private HashMap<String, String> mapping = new HashMap<>();
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
        mapping.put("p1", "");
        mapping.put("p2", "");
        mapping.put("p3", "");
        mapping.put("p4", "");
        hostActivityMap.put(packageName + ".ActivityS1", null);
        hostActivityMap.put(packageName + ".ActivityS2", null);
        hostActivityMap.put(packageName + ".ActivityS3", null);
        hostActivityMap.put(packageName + ".ActivityS4", null);
        hostActivityMap.put(packageName + ".ActivityS5", null);
        hostActivityMap.put(packageName + ".ActivityS6", null);
        hostActivityMap.put(packageName + ".ActivityS7", null);
        hostActivityMap.put(packageName + ".ActivityS8", null);
        hostActivityMap.put(packageName + ".ActivityS9", null);
        hostActivityMap.put(packageName + ".ActivityS10", null);


        hostServiceMap.put(packageName + ".ServiceS1", null);
        hostServiceMap.put(packageName + ".ServiceS2", null);
        hostServiceMap.put(packageName + ".ServiceS3", null);
        hostServiceMap.put(packageName + ".ServiceS4", null);
        hostServiceMap.put(packageName + ".ServiceS5", null);
        hostServiceMap.put(packageName + ".ServiceS6", null);
        hostServiceMap.put(packageName + ".ServiceS7", null);
        hostServiceMap.put(packageName + ".ServiceS8", null);
        hostServiceMap.put(packageName + ".ServiceS9", null);
        hostServiceMap.put(packageName + ".ServiceS10", null);
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

            case findRealClassName:
                bundle.putString(findRealClassName, findRealClassName(arg));
                return bundle;
            case clearSlot:
                hostActivityMap.put(arg, null);
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
        if (processName != null) {

            prefix = findProcessMapping(processName);
            if (TextUtils.isEmpty(mapping.get(prefix))) {
                Uri uri = Uri.parse("content://" + getContext().getPackageName() + "." + prefix);
                getContext().getContentResolver().query(uri, null, null, null, null);
                mapping.put(prefix, processName);
            }

        }
        Set<String> strings = hostActivityMap.keySet();
        for (String key : strings) {
            if (hostActivityMap.get(key + prefix) == null) {
                hostActivityMap.put(key + prefix, activityInfo.name);
                return key + prefix;
            }
        }

        return "";
    }

    private String findRealClassName(String key) {
        String real = hostActivityMap.get(key);
        if (TextUtils.isEmpty(real)) return key;
        else return real;
    }
}
