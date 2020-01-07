package com.me.hostlib.plugin;

import android.content.pm.ServiceInfo;

public class ServiceCache {

    private String pluginName;

    public ServiceCache(String pluginName, ServiceInfo ai) {
        this.pluginName = pluginName;
        this.ai = ai;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public ServiceInfo getAi() {
        return ai;
    }

    public void setAi(ServiceInfo ai) {
        this.ai = ai;
    }

    private ServiceInfo ai;
}
