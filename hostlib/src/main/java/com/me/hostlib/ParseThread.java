package com.me.hostlib;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.me.hostlib.utils.CloseableUtils;
import com.me.hostlib.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ParseThread extends Thread {

    private static final int OP_SRC = 0b1111;
    public static final int OP_ASSETS = 0b0001;
    public static final int OP_PARSE_APK = 0b1_0000;
    public static final int OP_MOUNT_PLUGIN = 0b1_0000_0000;


    private Context context;
    private String pluginInstallPath;
    private String soCache;
    private String opt;
    private String libs;
    private String pluginName;
    private ByteArrayOutputStream bout;
    private int operation = 0;

    public ParseThread(Context c, String pluginName, int operation) throws IOException {
        setName(pluginName);
        this.context = c;
        this.pluginName = pluginName;
        this.operation = operation;
        pluginInstallPath = Files.pluginDir(c, pluginName).getAbsolutePath();
        soCache = pluginInstallPath + File.separator + "soCache";
        opt = pluginInstallPath + File.separator + "opt";
        libs = pluginInstallPath + File.separator + "libs";
    }

    @Override
    public void run() {
        super.run();
        try {
            FileUtils.forceMkdir(new File(soCache));
            FileUtils.forceMkdir(new File(opt));
            FileUtils.forceMkdir(new File(libs));

            switch ((operation & OP_SRC)) {
                case OP_ASSETS:
                    int len = 0;
                    byte[] buff = new byte[512];
                    bout = new ByteArrayOutputStream();
                    InputStream in = context.getAssets().open(pluginName);
                    FileOutputStream out = new FileOutputStream(new File(pluginInstallPath, pluginName));
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                        bout.write(buff, 0, len);
                    }
                    bout.flush();
                    out.flush();
                    CloseableUtils.closeQuietly(in, out);
                    break;
                default:
            }
            switch (operation & OP_PARSE_APK) {
                case OP_PARSE_APK:
                    String dexPath = new File(pluginInstallPath, pluginName).getAbsolutePath();


                    boolean hasNativeLib = false;
                    boolean _64bit = false;
                    String _abi = "";
                    String _32abi = "";
                    String[] supportedAbis = Build.SUPPORTED_ABIS;

                    for (String abi :
                            supportedAbis) {
                        if (abi.equals("arm64-v8a") || abi.equals("x86_64")) {
                            _64bit = true;
                            break;
                        }
                        _64bit = false;
                    }
                    for (String abi :
                            supportedAbis) {
                        if (abi.equals("arm64-v8a") || abi.equals("x86_64")) {
                            _abi = abi;
                        }

                        if (abi.equals("armeabi-v7a") || abi.equals("x86")) {
                            _32abi = abi;
                        }
                    }

                    ZipFile zipFile = new ZipFile(dexPath);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        if (zipEntry.getName().startsWith("lib")) {
                            if (!hasNativeLib) {
                                hasNativeLib = true;
                            }
                            Log.e("ZipEntryName", zipEntry.getName());
                            String name = zipEntry.getName();
                            String[] nameSplit = name.split("/");
                            StringBuffer libPath = new StringBuffer();
                            for (int i = 1; i < nameSplit.length - 1; i++) {
                                libPath.append("/")
                                        .append(nameSplit[i]);
                            }

                            File libDir = new File(soCache + libPath.toString());
                            FileUtils.forceMkdir(libDir);
                            FileOutputStream out = new FileOutputStream(new File(libDir, nameSplit[nameSplit.length - 1]));
                            InputStream inputStream = zipFile.getInputStream(zipEntry);
                            int len = 0;
                            byte[] buff = new byte[512];
                            while ((len = inputStream.read(buff)) > 0) {
                                out.write(buff, 0, len);
                            }
                            out.flush();
                            CloseableUtils.closeQuietly(inputStream, out);
                        }
                    }
                    File libDir;
                    if (_64bit) {
                        libDir = new File(soCache + "/" + _abi);
                        if (!libDir.exists()) {
                            libDir = new File(soCache + "/" + _32abi);
                            if (!libDir.exists()) {
                                libDir = new File(soCache + "/armeabi");
                            }
                        }
                    } else {
                        libDir = new File(soCache + "/" + _32abi);
                        if (!libDir.exists()) {
                            libDir = new File(soCache + "/armeabi");
                        }
                    }
                    if (libDir.exists() && libDir.isDirectory()) {
                        File[] files = libDir.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            String name = files[i].getName();
                            Log.e("so name :", name);
                            Log.e("so src path", files[i].getAbsolutePath());
                            File soFile = new File(libs, name);
                            FileInputStream soIn = new FileInputStream(files[i]);
                            FileOutputStream soOut = new FileOutputStream(soFile);
                            int len = 0;
                            byte[] buff = new byte[512];
                            while ((len = soIn.read(buff)) > 0) {
                                soOut.write(buff, 0, len);
                            }
                            soOut.flush();
                            CloseableUtils.closeQuietly(soIn, soOut);
                        }
                    }


                    //TODO read receiver
            }

            switch (operation & OP_MOUNT_PLUGIN) {
                case OP_MOUNT_PLUGIN: {
                    String dexPath = new File(pluginInstallPath, pluginName).getAbsolutePath();
                    Plugins.sendAppInfo(pluginName, dexPath, opt, libs);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
