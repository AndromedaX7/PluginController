package com.me.test2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.me.pluginlib.receiver.PluginReceiver;

public class MyReceiver extends PluginReceiver {

    @Override
    public void _onReceive(Context context, Intent intent) {
//        val  context2  = super._onReceive(context, intent)
        String action = intent.getAction();
        Log.e("MyReceiver", "onReceive");
        Log.e("msg", "我要闹啦");
        Log.e("msg", context.getApplicationContext().getClass().getName());

        Log.e("MyReceiver", "onReceive");
        Log.e("msg", "我要闹啦!!!!!!!");
        Log.e("msg", context.getApplicationContext().getClass().getName());


    }
}
