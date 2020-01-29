package com.me.hostlib.plugin;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.me.hostlib.Plugins;

import java.util.HashMap;

public class ProviderService {
    private static ProviderService instance;
    private static HashMap<String, ProviderInfo> info = new HashMap<>();
    private static HashMap<String, ContentProvider> cache = new HashMap<>();
    private Context context;

    private ProviderService(Context context) {
        this.context = context;
    }


    public static void updateInfo(HashMap<String, ProviderInfo> info) {
        ProviderService.info.putAll(info);
    }

    public static ProviderService getInstance(Context context) {
        if (instance == null) {
            synchronized (ProviderService.class) {
                if (instance == null) {
                    instance = new ProviderService(context);
                }
            }
        }
        return instance;
    }

    public Bundle call(Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return null;
            } else {
                return provider.call(method, arg, extras);
            }
        }
        try {
            return client.call(method, arg, extras);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ContentProvider findProvider(Uri uri) {
        String authority = uri.getAuthority();
        ContentProvider contentProvider = cache.get(authority);
        if (contentProvider == null) {
            ProviderInfo providerInfo = info.get(authority);
            if (providerInfo == null) return null;
            HashMap<String, ClassLoader> allClassLoader = Plugins.getInstance().allClassLoader();
            String name = providerInfo.name;
            Class provider = null;
            String pk = "";
            for (String k : allClassLoader.keySet()) {
                ClassLoader classLoader = allClassLoader.get(k);
                try {
                    provider = classLoader.loadClass(name);
                    if (provider != null) break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (provider == null) {
                return null;
            }
            try {
                Object o = provider.newInstance();
                if (!(o instanceof ContentProvider)) return null;
                contentProvider = (ContentProvider) o;
                contentProvider.attachInfo(context, providerInfo);
                contentProvider.onCreate();
                cache.put(authority, contentProvider);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return contentProvider;
    }


    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return null;
            } else {
                return provider.query(uri, projection, selection, selectionArgs, sortOrder);
            }
        }
        try {
            return client.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getType(@NonNull Uri uri) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return null;
            } else {
                return provider.getType(uri);
            }
        }
        try {
            return client.getType(uri);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return null;
            } else {
                return provider.insert(uri, values);
            }
        }
        try {
            return client.insert(uri, values);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return 0;
            } else {
                return provider.delete(uri, selection, selectionArgs);
            }
        }
        try {
            return client.delete(uri, selection, selectionArgs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        ContentProviderClient client = queryProvider(uri);
        if (client == null) {
            ContentProvider provider = findProvider(uri);
            if (provider == null) {
                Log.e("get client error", uri.getAuthority());
                return 0;
            } else {
                return provider.update(uri, values, selection, selectionArgs);
            }
        }
        try {
            return client.update(uri, values, selection, selectionArgs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private ContentProviderClient queryProvider(Uri uri) {
        return context.getContentResolver().acquireContentProviderClient(uri);
    }
}
