package com.me.hostlib.plugin;

import android.content.pm.ActivityInfo;

public class ActivityCache {
    private String pluginName;
    private ActivityInfo ai ;

    public ActivityCache() {
    }

    public ActivityCache(String pluginName, ActivityInfo ai) {
        this.pluginName = pluginName;
        this.ai = ai;
    }

    public ActivityInfo getAi() {
        return ai;
    }

    public void setAi(ActivityInfo ai) {
        this.ai = ai;
    }



    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }
}
