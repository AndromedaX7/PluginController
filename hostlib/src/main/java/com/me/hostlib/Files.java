package com.me.hostlib;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static void deleteAllFile(File meAndSub) {
        if (meAndSub.isDirectory()) {
            File[] files = meAndSub.listFiles();
            for (File file : files) {
                deleteAllFile(file);
            }
            meAndSub.delete();
        } else {
            meAndSub.delete();
        }
    }

    public static String fileMd5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }
}
