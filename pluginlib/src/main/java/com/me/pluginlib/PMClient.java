package com.me.pluginlib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import static com.me.pluginlib.PluginManager.oProviderService;

public class PMClient {
    public static Bundle call(Context context, Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (oProviderService == null) {
            return context.getContentResolver().call(uri, method, arg, extras);
        }
        return call(uri, method, arg, extras);
    }

    public static Cursor query(Context context, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (oProviderService == null) {
            return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static String getType(Context context, @NonNull Uri uri) {
        if (oProviderService == null) {
            return context.getContentResolver().getType(uri);
        }
        return getType(uri);
    }

    public static Uri insert(Context context, @NonNull Uri uri, @Nullable ContentValues values) {
        if (oProviderService == null) {
            return context.getContentResolver().insert(uri, values);
        }
        return insert(uri, values);
    }

    public static int delete(Context context, @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (oProviderService == null) {
            return context.getContentResolver().delete(uri, selection, selectionArgs);
        }
        return delete(uri, selection, selectionArgs);
    }

    public static int update(Context context, @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (oProviderService == null) {
            return context.getContentResolver().update(uri, values, selection, selectionArgs);
        }
        return update(uri, values, selection, selectionArgs);
    }

    public static OutputStream openOutputStream(Context context, Uri uri) throws FileNotFoundException {
        if (oProviderService == null) {
            return context.getContentResolver().openOutputStream(uri);
        }
        return openOutputStream(uri);
    }

    public static InputStream openInputStream(Context context, Uri uri) throws FileNotFoundException {
        if (oProviderService == null) {
            return context.getContentResolver().openInputStream(uri);
        }
        return openInputStream(uri);
    }

    private static Bundle call(Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (oProviderService == null) {
            return null;
        }
        try {
            Class classProviderService=oProviderService.getClass();
//            classProviderService.getDeclaredMethod("call",Uri.class,String.class)
            return (Bundle) ReflectUtils.ccInvokeMethod(oProviderService, "call", new Class[]{Uri.class,String.class, String.class, Bundle.class}, uri, method, arg, extras);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (oProviderService == null) {
            return null;
        }
        try {
            return (Cursor) ReflectUtils.ccInvokeMethod(oProviderService, "query", new Class[]{Uri.class, String[].class, String.class, String[].class, String.class}, uri, projection, selection, selectionArgs, sortOrder);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getType(@NonNull Uri uri) {
        if (oProviderService == null) {
            return null;
        }
        try {
            return (String) ReflectUtils.ccInvokeMethod(oProviderService, "getType", new Class[]{Uri.class}, uri);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (oProviderService == null) {
            return null;
        }

        try {
            return (Uri) ReflectUtils.ccInvokeMethod(oProviderService, "insert", new Class[]{Uri.class, ContentValues.class}, uri, values);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (oProviderService == null) {
            return 0;
        }
        try {
            return (int) ReflectUtils.ccInvokeMethod(oProviderService, "delete", new Class[]{Uri.class, String.class, String[].class}, uri, selection, selectionArgs);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (oProviderService == null) {
            return 0;
        }
        try {
            return (int) ReflectUtils.ccInvokeMethod(oProviderService, "update", new Class[]{Uri.class, ContentValues.class, String.class, String[].class}, uri, values, selection, selectionArgs);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }


//    private ContentProviderClient queryProvider(Uri uri) {
//        return context.getContentResolver().acquireContentProviderClient(uri);
//    }

    private static OutputStream openOutputStream(Uri uri) throws FileNotFoundException {
        if (oProviderService == null) {
            return null;
        }

        try {
            return (OutputStream) ReflectUtils.ccInvokeMethod(oProviderService, "openOutputStream", new Class[]{Uri.class}, uri);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream openInputStream(Uri uri) throws FileNotFoundException {
        if (oProviderService == null) {
            return null;
        }
        try {
            return (InputStream) ReflectUtils.ccInvokeMethod(oProviderService, "openInputStream", new Class[]{Uri.class}, uri);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
