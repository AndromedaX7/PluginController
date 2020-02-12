package com.me.hostlib.plugin;

import android.content.ContentProviderClient;
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
            synchronized (ActivitySlotManager.class) {
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
                    client().call(ProcessMapping.clearSlot, slotName, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return className;
        } else {
            return slotName;
        }
    }
}
