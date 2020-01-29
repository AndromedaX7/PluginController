package com.me.pluginlib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.me.pluginlib.PluginManager;

public   class PluginReceiver extends BroadcastReceiver {

    public PluginReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        _onReceive(myContext(context), intent);
    }

    public void _onReceive(Context context, Intent intent) {

    }

    private Context myContext(Context context) {
        Log.e("PluginManager::",PluginManager.sApplicationContext+">>>");
        if (PluginManager.sApplicationContext!=null){
             return  PluginManager.sApplicationContext;
        }
        return  context;
    }


}

