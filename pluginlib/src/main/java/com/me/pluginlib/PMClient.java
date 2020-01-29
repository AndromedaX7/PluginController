package com.me.pluginlib;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PMClient {
    public static Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    public static String getType(@NonNull Uri uri) {
        return null;
    }

    public static  Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    public static int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static  Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        return null;
    }
}