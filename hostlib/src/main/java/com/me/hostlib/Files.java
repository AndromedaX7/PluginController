package com.me.hostlib;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class Files {
    public static File pluginRoot(Context context) throws IOException {
        File filesDir = context.getFilesDir();
        File pluginRoot = new File(filesDir, "pluginRoot");
        if (!pluginRoot.exists()) {
            pluginRoot.mkdir();
            return pluginRoot;
        } else {
            if (pluginRoot.isDirectory()) {
                return pluginRoot;
            } else {
                throw new IOException(pluginRoot.getAbsolutePath() + " is not a directory!");
            }
        }

    }

    public static File pluginDir(Context c, String pluginName) throws IOException {
        File file = pluginRoot(c);
        File dir = new File(file, pluginName);
        if (!dir.exists()) {
            dir.mkdir();
            return dir;
        } else {
            if (dir.isDirectory()) {
                return dir;
            } else {
                throw new IOException(dir.getAbsolutePath() + " is not a directory!");
            }
        }

    }
}
