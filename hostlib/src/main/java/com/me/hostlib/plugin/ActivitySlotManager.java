package com.me.hostlib.plugin;

import android.content.ContentProviderClient;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.me.hostlib.Plugins;
import com.me.hostlib.ProcessMapping;

public class ActivitySlotManager {
    private static final Uri uri = Uri.parse("content://" + Plugins.getInstance().getAppContext().getPackageName() + ".mapping");
    private static volatile ActivitySlotManager instance;
    private ContentProviderClient client;

    private ActivitySlotManager() {

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
        Bundle bundle = null;
        try {
            bundle = client().call(ProcessMapping.findRealClassName, slotName, null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String className = "";
        if (bundle != null && !TextUtils.isEmpty((className = bundle.getString(ProcessMapping.findRealClassName)))) {
            try {
                if (!className.equals(slotName))
                    client.call(ProcessMapping.clearSlot, slotName, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return className;
        } else {
            return slotName;
        }
    }

    public String dispatchSlot(ActivityInfo activityInfo) {
        ContentProviderClient client = client();
        Bundle bundle = new Bundle();

        bundle.putParcelable(ProcessMapping.mappingActivity, activityInfo);
        try {
            Bundle mappingActivity = client.call(ProcessMapping.mappingActivity, null, bundle);
            if (mappingActivity != null) {
                String key = mappingActivity.getString(ProcessMapping.mappingActivity, "");
                if (TextUtils.isEmpty(key)) {
//   launch Empty
                } else {
                    return key;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }


    private ContentProviderClient client() {
        if (client == null)
            client = Plugins.getInstance().getAppContext().getContentResolver().acquireContentProviderClient(uri);
        return client;
    }
}
