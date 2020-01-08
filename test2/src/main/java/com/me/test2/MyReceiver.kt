package com.me.test2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.e("MyReceiver","onReceive")
        Log.e("msg","我要闹啦")
        Log.e("msg",context.applicationContext::class.java.name )

    }
}
