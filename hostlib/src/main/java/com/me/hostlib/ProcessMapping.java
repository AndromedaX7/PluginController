package com.me.hostlib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ProcessMapping extends ContentProvider {
    public static final String mappingProcess = "mappingProcess";
    public static final String internal = "internal";
    public static final String from = "from";
    HashMap<String, String> mapping = new HashMap<>();

    public ProcessMapping() {
        mapping.put("p1","");
        mapping.put("p2","");
        mapping.put("p3","");
        mapping.put("p4","");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String _internal = values.getAsString(internal);
        String _from = values.getAsString(from);
        if (TextUtils.isEmpty(_from)) return uri;
        if (TextUtils.isEmpty(_internal)) return uri;
        mapping.put(_internal, _from);
        return uri;
    }

    @Override
    public boolean onCreate() {
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
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle bundle = new Bundle();
        switch (method) {
            case mappingProcess:
                for (String m : mapping.keySet()) {
                    if (mapping.get(m).equals(arg)) {
                        bundle.putString(mappingProcess, m);
                        return bundle;
                    }
                }

                for (String m : mapping.keySet()) {
                    if (TextUtils.isEmpty(mapping.get(m))) {
                        bundle.putString(mappingProcess, m);
                        return bundle;
                    }
                }


                break;
        }


        return bundle;
    }



}
