package com.me.hostlib.plugin;

import java.util.ArrayList;

public class AndroidManifest {
    private String packages;
    private Application application;

    public String getPackage() {
        return packages;
    }

    public void setPackage(String packages) {
        this.packages = packages;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public enum ComponentType {
        Unknown,
        Activity,
        Service,
        Receiver,
        Application,
        Provider
    }

    public static class Application extends Component {
        private ArrayList<Component> activity = new ArrayList<>();
        private ArrayList<Component> service = new ArrayList<>();
        private ArrayList<Component> receiver = new ArrayList<>();
        private ArrayList<Component> provider = new ArrayList<>();

        @Override
        public ComponentType type() {
            return ComponentType.Application;
        }

        public ArrayList<Component> getActivity() {
            return activity;
        }

        public ArrayList<Component> getService() {
            return service;
        }

        public ArrayList<Component> getReceiver() {
            return receiver;
        }

        public ArrayList<Component> getProvider() {
            return provider;
        }
    }

    public static class Activity extends Component {
        @Override
        public ComponentType type() {
            return ComponentType.Activity;
        }
    }

    public static class Service extends Component {
        @Override
        public ComponentType type() {
            return ComponentType.Service;
        }
    }

    public static class Receiver extends Component {
        @Override
        public ComponentType type() {
            return ComponentType.Receiver;
        }
    }

    public static class Provider extends Component {
        @Override
        public ComponentType type() {
            return ComponentType.Provider;
        }
    }

    public static class Component {
        private String name;
        private boolean enabled;
        private boolean exported;
        private ArrayList<IntentFilter> intentFilters = new ArrayList<>();

        public ComponentType type() {
            return ComponentType.Unknown;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isExported() {
            return exported;
        }

        public void setExported(boolean exported) {
            this.exported = exported;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<IntentFilter> getIntentFilters() {
            return intentFilters;
        }

        public void setIntentFilters(ArrayList<IntentFilter> intentFilters) {
            this.intentFilters = intentFilters;
        }
    }

    public static class IntentFilter {
        private ArrayList<String> action = new ArrayList<>();
        private ArrayList<String> category = new ArrayList<>();
        private ArrayList<Data> data = new ArrayList<>();

        public ArrayList<String> getAction() {
            return action;
        }

        public void setAction(ArrayList<String> action) {
            this.action = action;
        }

        public ArrayList<String> getCategory() {
            return category;
        }

        public void setCategory(ArrayList<String> category) {
            this.category = category;
        }
    }

    public static class Data {
        private String scheme;
        private String host;
        private String port;
        private String path;
        private String pathPrefix;
        private String pathPattern;
        private String mimeType;

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPathPrefix() {
            return pathPrefix;
        }

        public void setPathPrefix(String pathPrefix) {
            this.pathPrefix = pathPrefix;
        }

        public String getPathPattern() {
            return pathPattern;
        }

        public void setPathPattern(String pathPattern) {
            this.pathPattern = pathPattern;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}
