package com.me.test2

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.me.pluginlib.service.PluginService

class MyService2 : PluginService() {
    private companion object {
        val TAG = MyService2::class.java.name
    }

    override fun onBind(intent: Intent): IBinder {
        return MyServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")

        val intExtra = intent?.getIntExtra("start", 0)
        when (intExtra) {
            1 -> {
                startActivity(
                    Intent(
                        this,
                        ImageActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            2 -> {

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }


}
