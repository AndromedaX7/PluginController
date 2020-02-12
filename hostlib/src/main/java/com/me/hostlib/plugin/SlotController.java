package com.me.hostlib.plugin;

import android.content.ContentProviderClient;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.me.hostlib.Plugins;
import com.me.hostlib.ProcessMapping;

public class SlotController {
    private static final Uri uri = Uri.parse("content://" + Plugins.getInstance().getAppContext().getPackageName() + ".mapping");
    private static volatile SlotController instance;
    private ContentProviderClient client;

    private SlotController() {

    }

    public static SlotController getInstance() {
        if (instance == null) {
            synchronized (SlotController.class) {
                if (instance == null)
                    instance = new SlotController();
            }
        }
        return instance;
    }

    private ContentProviderClient client() {
        if (client == null)
            client = Plugins.getInstance().getAppContext().getContentResolver().acquireContentProviderClient(uri);
        return client;
    }

    public void cleanServiceSlot(String name) {
        try {
            client().call(ProcessMapping.clearServiceSlot, name, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String findSlotClass(String slotName) {
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
                    client().call(ProcessMapping.clearActivitySlot, slotName, null);
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


    public String dispatchSlot(ServiceInfo serviceInfo) {
        ContentProviderClient client = client();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ProcessMapping.mappingService, serviceInfo);
        try {
            Bundle mappingService = client.call(ProcessMapping.mappingService, null, bundle);
            if (mappingService != null) {
                String key = mappingService.getString(ProcessMapping.mappingService, "");
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
}
