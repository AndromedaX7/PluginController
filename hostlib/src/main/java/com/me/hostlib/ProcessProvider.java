package com.me.hostlib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ProcessProvider extends ContentProvider {
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public boolean onCreate() {
        Plugins.getInstance().setNewHandler();
        Plugins.getInstance().onApplicationCreateLocked();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static class ProcessP1 extends ProcessProvider {
    }
    public static class ProcessP2 extends ProcessProvider {
    }
    public static class ProcessP3 extends ProcessProvider {
    }
    public static class ProcessP4 extends ProcessProvider {
    }
    public static class ProcessP5 extends ProcessProvider {
    }
    public static class ProcessP6 extends ProcessProvider {
    }
    public static class ProcessP7 extends ProcessProvider {
    }
    public static class ProcessP8 extends ProcessProvider {
    }
    public static class ProcessP9 extends ProcessProvider {
    }
    public static class ProcessP10 extends ProcessProvider {
    }
}
