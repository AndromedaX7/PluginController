package com.me.pluginlib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.me.pluginlib.PluginContext;
import com.me.pluginlib.PluginManager;

public abstract class PluginService extends Service {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new PluginContext(base));
    }

    @Override
    public void onDestroy() {
        if (PluginManager.sClassLoader != null) {
            PluginManager.cleanServiceSlot(this.getClass().getName());
        }
        super.onDestroy();
    }


}
