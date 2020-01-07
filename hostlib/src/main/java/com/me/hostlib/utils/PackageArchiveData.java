package com.me.hostlib.utils;

public class PackageArchiveData {

   private  String pluginName;
   private  String dexPath;
   private  String opt;
   private  String libs;

    public PackageArchiveData(String pluginName, String dexPath, String opt, String libs) {
        this.pluginName = pluginName;
        this.dexPath = dexPath;
        this.opt = opt;
        this.libs = libs;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getDexPath() {
        return dexPath;
    }

    public void setDexPath(String dexPath) {
        this.dexPath = dexPath;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getLibs() {
        return libs;
    }

    public void setLibs(String libs) {
        this.libs = libs;
    }
}
