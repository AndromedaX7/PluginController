package com.me.hostlib.plugin;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ManifestParser {
    private ArrayList<AndroidManifest> manifests = new ArrayList<>();
    private static ManifestParser instance;
    private HashMap<String, AndroidManifest.Receiver> cached = new HashMap<>();

    public static ManifestParser getInstance() {
        if (instance == null) {
            synchronized (ManifestParser.class) {
                if (instance == null) {
                    instance = new ManifestParser();
                }
            }
        }
        return instance;
    }

    private ManifestParser() {

    }

    public static void installPluginReceiver(Application context, String pluginName, PackageInfo packageInfo, Resources resources) {
        try {
            getInstance().parse(resources.getAssets().openXmlResourceParser("AndroidManifest.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void parse(XmlResourceParser parser) {
        AndroidManifest manifest = new AndroidManifest();
        AndroidManifest.Application application =new AndroidManifest.Application();
        AndroidManifest.Component currentComponent;

        String publicNamespace="http://schemas.android.com/apk/res/android";
        try {
            int eventType = parser.getEventType();


            int depth = parser.getDepth();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
//                        for (int i = 0; i < 10; i++) {
//                            String attributeNamespace = parser.getAttributeNamespace(i);
//                            Log.i("attributeNamespace", attributeNamespace);
//                        }
                        if (parser.getName().equals("manifest")) {
                            String aPackage = parser.getAttributeValue(null, "package");
                            Log.i("package", aPackage);
                            manifest.setPackage(aPackage);
                        }else if (parser.getName().equals("application")){


                        } else if (parser.getName().equals("activity")) {
                            currentComponent=new AndroidManifest.Activity();
                            application.getActivity().add(currentComponent);
                            currentComponent.setName( parser.getAttributeValue(publicNamespace, "name"));
                        } else if (parser.getName().equals("service")){
                            currentComponent=new AndroidManifest.Service();
                            currentComponent.setName( parser.getAttributeValue(publicNamespace, "name"));
                            application.getService().add(currentComponent);
                        }else if (parser.getName().equals("receiver")){
                            currentComponent=new AndroidManifest.Receiver();
                            currentComponent.setName( parser.getAttributeValue(publicNamespace, "name"));
                            application.getReceiver().add(currentComponent);
                        }else if (parser.getName().equals("provider")){
                            currentComponent=new AndroidManifest.Provider();
                            currentComponent.setName( parser.getAttributeValue(publicNamespace, "name"));
                            application.getProvider().add(currentComponent);
                        }else if (parser.getName().equals("intent-filter")){

                        }else if (parser.getName().equals("action")){

                        }else if (parser.getName().equals("category")){

                        }
                    }
                    break;
                }


                eventType = parser.next();
            }
            manifests.add(manifest);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
}
