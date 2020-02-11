package com.me.hostlib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class PluginManagerService extends Service {
    private static final String TAG = "pms";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= 18) {
//            NotificationChannel channel = null;
//            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//            if (Build.VERSION.SDK_INT >= 28) {
//                channel = new NotificationChannel("ccid", "ccid", NotificationManager.IMPORTANCE_HIGH);
//                nm.createNotificationChannel(channel);
//            }
//            NotificationCompat.Builder not = new NotificationCompat.Builder(this, "ccid");
//
//            startForeground(1, not.build());
//
//        }

//        stopForeground(true);
    }

//    private void loadCurrent() {
//        Iterator<String> iterator = changed.iterator();
//        while (iterator.hasNext()) {
//            String cc = iterator.next();
//            parseCommand(cc);
//            iterator.remove();
//        }
//
//    }


}
