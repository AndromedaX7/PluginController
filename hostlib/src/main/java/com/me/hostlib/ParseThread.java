package com.me.hostlib;

import android.content.Context;

import com.me.hostlib.utils.CloseableUtils;
import com.me.hostlib.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ParseThread extends Thread {

    private static final int OP_SRC = 0b1111;
    public static final int OP_ASSETS = 0b0001;
    public static final int OP_PARSE_APK = 0b1_0000;
    public static final int OP_MOUNT_PLUGIN = 0b1_0000_0000;


    private Context context;
    private String pluginInstallPath;
    private String dex;
    private String opt;
    private String libs;
    private String pluginName;
    private ByteArrayOutputStream bout;
    private int operation = 0;

    public ParseThread(Context c, String pluginName, int operation ) throws IOException {
        setName(pluginName);
        this.context = c;
        this.pluginName = pluginName;
        this.operation = operation;
        pluginInstallPath = Files.pluginDir(c, pluginName).getAbsolutePath();
        dex = pluginInstallPath + File.separator + "dex";
        opt = pluginInstallPath + File.separator + "opt";
        libs = pluginInstallPath + File.separator + "libs";
    }

    @Override
    public void run() {
        super.run();
        try {
            FileUtils.forceMkdir(new File(dex));
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
                    Plugins.sendAppInfo(pluginName, dexPath, opt, libs);

                    //TODO read receiver
                    //TODO unzip so

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
