package com.me.pluginlib.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.me.pluginlib.PluginContext;
import com.me.pluginlib.PluginManager;

public class PluginAppCompatActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new PluginContext(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PluginManager.sClassLoader != null) {
            Intent intent = getIntent();
            ActivityInfo activityInfo = intent.getParcelableExtra("ActivityInfo");
            setTheme(activityInfo != null && activityInfo.theme != 0 ? activityInfo.theme : getApplicationInfo().theme);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivity(intent);
        } else
            super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivity(intent, options);
        } else
            super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivityForResult(intent, requestCode);
        } else
            super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (PluginManager.sClassLoader != null) {
            PluginManager.prepareStartActivity(intent);
            super.startActivityForResult(intent, requestCode, options);
        } else
            super.startActivityForResult(intent, requestCode, options);
    }

}
