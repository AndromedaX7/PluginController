package com.me.hostlib.plugin;

import java.util.ArrayList;

public class ReceiverRedirectHolder {

    private String ac;
    private String pluginName;
    private String receiverName;

    public ReceiverRedirectHolder(String ac, String pluginName, String receiverName) {
        this.ac = ac;
        this.pluginName = pluginName;
        this.receiverName = receiverName;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public static String AcCreator(ArrayList<String> action, ArrayList<String> category) {
        return action.toString() + ":" + category.toString();
    }
}
