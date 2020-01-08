package com.me.hostlib.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.me.hostlib.Plugins;
import com.me.hostlib.xml.SXmlPullParser;
import com.me.hostlib.xml.XmlBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiverParser {
    private static final String TAG="ReceiverParser";
    private static   ReceiverParser ourInstance  ;
    private static List<SubReceiver> sBroadcastReceiverList = new ArrayList<>();
    public static ReceiverParser getInstance() {
        if (ourInstance == null) {
            synchronized (ReceiverParser.class){
                if (ourInstance == null) {
                    ourInstance = new ReceiverParser();
                }
            }
        }
        return ourInstance;
    }

    private ReceiverParser() {
    }

    public static void installPluginReceiver(Context context, String pluginName,PackageInfo pInfo, Resources resource) {
        ActivityInfo[] receivers = pInfo.receivers;
//        clearBroadcastReceiverList(context);
//        sBroadcastReceiverList.clear();
        ////////

        if (receivers ==null) return;
        final Map<String, HashMap<String, ArrayList<String>>> receiverMap = new HashMap<>();
        XmlBean xmlBean;
        XmlBean rootXml = parseAndroidManifest(resource);
        xmlBean = getXmlBeanByName("manifest", rootXml);
        if (xmlBean != null) {
            xmlBean = getXmlBeanByName("application", xmlBean);
            if (xmlBean != null) {
                XmlBean receiverXml = getXmlBeanByName("receiver", xmlBean);
                if (receiverXml != null) {
                    parserXmlBeanByName("receiver", xmlBean, new CallBack() {
                        @Override
                        public void onGetXml(final XmlBean receiver) {
                            String recName = receiver.getAttributeMap().get("name");
                            if (recName != null) {
                                XmlBean intentFilter = getXmlBeanByName("intent-filter", receiver);
                                if (intentFilter != null) {
                                    if (receiverMap.get(recName) == null) {
                                        receiverMap.put(recName, new HashMap<String, ArrayList<String>>());
                                    }
                                    if (intentFilter.getSon() != null) {
                                        if (receiverMap.get(recName).get(intentFilter.getSon().name) == null) {
                                            receiverMap.get(recName).put(intentFilter.getSon().name, new ArrayList<String>());
                                        }
                                        receiverMap.get(recName).get(intentFilter.getSon().name).add(intentFilter.getSon().getAttributeMap().get("name"));
                                        for (int i = 0; i < intentFilter.getSon().getYoungerBrother().size(); i++) {
                                            XmlBean filter = intentFilter.getSon().getYoungerBrother().get(i);
                                            if (receiverMap.get(recName).get(filter.name) == null) {
                                                receiverMap.get(recName).put(filter.name, new ArrayList<String>());
                                            }
                                            receiverMap.get(recName).get(filter.name).add(filter.getAttributeMap().get("name"));
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
        /////
        for (ActivityInfo receiverInfo : receivers) {
            Log.w(TAG,"Plugins ActivityInfo receivers>" + receiverInfo.name + " " + receiverInfo.flags);
            try {
                Class receiver =  Plugins.getInstance().getClassLoader(pluginName).loadClass(receiverInfo.name);
                IntentFilter intentFilter = new IntentFilter();
                HashMap<String, ArrayList<String>> attributeMap = receiverMap.get(receiverInfo.name);
                if (attributeMap != null) {
                    if (attributeMap.get("action") != null) {
                        ArrayList<String> actionList = attributeMap.get("action");
                        if (actionList != null) {
                            for (String action : actionList) {
                                Log.w(TAG,"receiver: " + receiverInfo.name + " action: " + action);
                                intentFilter.addAction(action);
                            }
                        }
                    }
                    if (attributeMap.get("category") != null) {
                        ArrayList<String> categoryList = attributeMap.get("category");
                        if (categoryList != null) {
                            for (String category : categoryList) {
                                Log.w(TAG,"receiver: " + receiverInfo.name + " category: " + category);
                                intentFilter.addCategory(category);
                            }
                        }
                    }
                }
                addSubBroadcastReceiver(new SubReceiver((BroadcastReceiver) receiver.newInstance(), intentFilter));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        registerBroadcastReceiver(context);
    }

    private static XmlBean parseAndroidManifest(Resources resource) {
        XmlBean xmlBean = null;
        try {
            final XmlResourceParser xml = resource.getAssets().openXmlResourceParser("AndroidManifest.xml");
            xmlBean = SXmlPullParser.parse(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (xmlBean == null) xmlBean = new XmlBean(null);
        return xmlBean;
    }

    private static void addSubBroadcastReceiver(SubReceiver subReceiver) {
        sBroadcastReceiverList.add(subReceiver);
    }
    
    private static XmlBean getXmlBeanByName(String name, XmlBean xmlBean) {
        if (name.equals(xmlBean.getName())) {
            return xmlBean;
        } else if (xmlBean.getSon() != null) {
            if (name.equals(xmlBean.getSon().getName())) {
                return xmlBean.getSon();
            } else {
                for (int i = 0; i < xmlBean.getSon().getYoungerBrother().size(); i++) {
                    if (name.equals(xmlBean.getSon().getYoungerBrother().get(i).getName())) {
                        return xmlBean.getSon().getYoungerBrother().get(i);
                    }
                }
            }
        } else {
            for (int i = 0; i < xmlBean.getYoungerBrother().size(); i++) {
                if (name.equals(xmlBean.getYoungerBrother().get(i).getName())) {
                    return xmlBean.getYoungerBrother().get(i);
                }
            }
        }
        return null;
    }

    public static void parserXmlBeanByName(String name, XmlBean xmlBean, CallBack callBack) {
        if (name.equals(xmlBean.getName())) {
            callBack.onGetXml(xmlBean);
        } else if (xmlBean.getSon() != null) {
            if (name.equals(xmlBean.getSon().getName())) {
                callBack.onGetXml(xmlBean.getSon());
            } else {
                for (int i = 0; i < xmlBean.getSon().getYoungerBrother().size(); i++) {
                    if (name.equals(xmlBean.getSon().getYoungerBrother().get(i).getName())) {
                        callBack.onGetXml(xmlBean.getSon().getYoungerBrother().get(i));
                    }
                }
            }
        } else {
            for (int i = 0; i < xmlBean.getYoungerBrother().size(); i++) {
                if (name.equals(xmlBean.getYoungerBrother().get(i).getName())) {
                    callBack.onGetXml(xmlBean.getYoungerBrother().get(i));
                }
            }
        }
    }
    public interface CallBack {
        void onGetXml(XmlBean xmlBean);
    }

    private static void registerBroadcastReceiver(Context context) {
        for (SubReceiver subReceiver : sBroadcastReceiverList) {
            context.getApplicationContext().registerReceiver(subReceiver.getReceiver(), subReceiver.getFilter() != null ?
                    subReceiver.getFilter() : new IntentFilter());
            Log.w(TAG,"subReceiver>" + subReceiver.getReceiver().getClass().getSimpleName());
        }
    }

    private static void unRegisterBroadcastReceiver(Context context) {
        for (SubReceiver subReceiver : sBroadcastReceiverList) {
            context.getApplicationContext().unregisterReceiver(subReceiver.getReceiver());
        }
    }
}
