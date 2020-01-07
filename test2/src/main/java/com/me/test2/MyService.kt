package com.me.test2

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.me.pluginlib.service.PluginService

class MyService : PluginService() {
    private companion object {
        val TAG = MyService::class.java.name
    }

    override fun onBind(intent: Intent): IBinder {
        return null!!
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
