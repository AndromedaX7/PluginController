package com.me.hostlib.plugin;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ManifestParser {
    private static ManifestParser instance;
    private ArrayList<AndroidManifest> manifests = new ArrayList<>();
    //  key  plugin name ,
    private HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> components = new HashMap<>();
    //    key : className
    private HashMap<String, ComponentInfo> pInfoCache = new HashMap<>();

    private ManifestParser() {

    }

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

    public static void installPluginInformation(Application context, String pluginName, PackageInfo packageInfo, Resources resources) {
        try {
            AndroidManifest manifest = getInstance().parse(resources.getAssets().openXmlResourceParser("AndroidManifest.xml"));
            genComponentFilter(manifest);
            genInfoCache(packageInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void genInfoCache(PackageInfo packageInfo) {
        ActivityInfo[] activities = packageInfo.activities;
        ServiceInfo[] services = packageInfo.services;
        ActivityInfo[] receivers = packageInfo.receivers;
        ProviderInfo[] providers = packageInfo.providers;
        if (activities != null) {
            for (ActivityInfo activity : activities) {
                String name = activity.name;
                getInstance().pInfoCache.put(name, activity);
            }
        }
        if (services != null) {
            for (ServiceInfo service : services) {
                String name = service.name;
                getInstance().pInfoCache.put(name, service);
            }
        }
        if (receivers != null) {
            for (ActivityInfo receiver : receivers) {
                String name = receiver.name;
                getInstance().pInfoCache.put(name, receiver);
            }
        }
        if (providers != null) {
            for (ProviderInfo provider : providers) {
                String name = provider.name;
                getInstance().pInfoCache.put(name, provider);
            }
        }

    }

    private static void genComponentFilter(AndroidManifest manifest) {
        HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> iftc = ManifestParser.getInstance().components;
        AndroidManifest.Application app = manifest.getApplication();
        ArrayList<AndroidManifest.Component> activity = app.getActivity();
        ArrayList<AndroidManifest.Component> service = app.getService();
        ArrayList<AndroidManifest.Component> receiver = app.getReceiver();
        ArrayList<AndroidManifest.Component> provider = app.getProvider();

        for (AndroidManifest.Component c :
                activity) {
            for (AndroidManifest.IntentFilter ift :
                    c.getIntentFilters()) {
                iftc.put(ift, c);
            }
        }
        for (AndroidManifest.Component c :
                service) {
            for (AndroidManifest.IntentFilter ift :
                    c.getIntentFilters()) {
                iftc.put(ift, c);
            }
        }
        for (AndroidManifest.Component c :
                receiver) {
            for (AndroidManifest.IntentFilter ift :
                    c.getIntentFilters()) {
                iftc.put(ift, c);
            }
        }
        for (AndroidManifest.Component c :
                provider) {
            for (AndroidManifest.IntentFilter ift :
                    c.getIntentFilters()) {
                iftc.put(ift, c);
            }
        }
        Log.e("parse src ok", "!!");
    }

    public HashMap<String, ComponentInfo> getInfoCache() {
        return pInfoCache;
    }

    public HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> getComponents() {
        return components;
    }

    public AndroidManifest parse(XmlResourceParser parser) {
        AndroidManifest manifest = new AndroidManifest();
        AndroidManifest.Application application = new AndroidManifest.Application();
        manifest.setApplication(application);
        AndroidManifest.Component currentComponent = null;
        AndroidManifest.IntentFilter ift = null;
        String publicNamespace = "http://schemas.android.com/apk/res/android";
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
//                        for (int i = 0; i < 10; i++) {
//                            String attributeNamespace = parser.getAttributeNamespace(i);
//                            Log.i("attributeNamespace", attributeNamespace);
//                        }
                        if (parser.getName().equals("manifest")) {
                            String aPackage = parser.getAttributeValue(null, "package");
                            Log.w("package", aPackage);
                            manifest.setPackage(aPackage);
                        } else if (parser.getName().equals("application")) {
                            application.setName(parser.getAttributeValue(publicNamespace, "name"));
                            Log.w("application", "================ start =================");
                        } else if (parser.getName().equals("activity")) {
                            currentComponent = new AndroidManifest.Activity();
                            application.getActivity().add(currentComponent);
                            currentComponent.setName(parser.getAttributeValue(publicNamespace, "name"));
                            Log.w("activity", currentComponent.getName());
                        } else if (parser.getName().equals("service")) {
                            currentComponent = new AndroidManifest.Service();
                            currentComponent.setName(parser.getAttributeValue(publicNamespace, "name"));
                            application.getService().add(currentComponent);
                            Log.w("service", currentComponent.getName());
                        } else if (parser.getName().equals("receiver")) {
                            currentComponent = new AndroidManifest.Receiver();
                            currentComponent.setName(parser.getAttributeValue(publicNamespace, "name"));
                            application.getReceiver().add(currentComponent);
                            Log.w("receiver", currentComponent.getName());
                        } else if (parser.getName().equals("provider")) {
                            currentComponent = new AndroidManifest.Provider();
                            currentComponent.setName(parser.getAttributeValue(publicNamespace, "name"));
                            application.getProvider().add(currentComponent);
                            Log.w("provider", currentComponent.getName());
                        } else if (parser.getName().equals("intent-filter")) {
                            ArrayList<AndroidManifest.IntentFilter> intentFilters = currentComponent.getIntentFilters();
                            ift = new AndroidManifest.IntentFilter();
                            intentFilters.add(ift);
                            Log.w("intent-filter", "================ start =================");
                        } else if (parser.getName().equals("action")) {
                            ArrayList<String> actions = ift.getAction();
                            String action = parser.getAttributeValue(publicNamespace, "name");
                            actions.add(action);
                            Log.w("action", action);
                        } else if (parser.getName().equals("category")) {
                            ArrayList<String> categories = ift.getCategory();
                            String category = parser.getAttributeValue(publicNamespace, "name");
                            categories.add(category);
                            Log.w("category", category);
                        } else if (parser.getName().equals("data")) {
                            String data = "----";
                            Log.w("data", data);
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG: {
                        if (parser.getName().equals("manifest")) {
                        } else if (parser.getName().equals("application")) {
                            Log.w("application", "================ end =================");
                        } else if (parser.getName().equals("intent-filter")) {
                            ift = null;
                            Log.w("intent-filter", "================ end =================");
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
        return manifest;
    }


    public HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> findByAction(String action, HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> in) {
        HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> out = new HashMap<>();
        for (AndroidManifest.IntentFilter ift : in.keySet()) {
            if (ift.getAction().contains(action)) {
                out.put(ift, in.get(ift));
            }
        }
        return out;
    }


    public HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> findByCategory(Collection<String> categories, HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> in) {
        HashMap<AndroidManifest.IntentFilter, AndroidManifest.Component> out = new HashMap<>();
        for (AndroidManifest.IntentFilter ift : in.keySet()) {
            if (ift.getCategory().containsAll(categories)) {
                out.put(ift, in.get(ift));
            }
        }
        return out;
    }
}
