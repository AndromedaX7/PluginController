package com.me.test2

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.me.pluginlib.service.PluginService

class MyService : Service() {
    private companion object {
        val TAG = MyService::class.java.name
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }
}
