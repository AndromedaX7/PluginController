package com.me.host

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.me.hostlib.Plugins

class TestApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Plugins.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        Plugins.getInstance().installOrLoad(this,"nativelib-debug.apk")
        Plugins.getInstance().installOrLoad(this,"cp-2.8.7.apk")
    }

    fun _getProcessName () :String {
        val myPid = Process.myPid()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = am.runningAppProcesses
        for (proc in runningAppProcesses){
            if (proc .pid ==myPid) {
                return  proc.processName
            }
        }


        return  ""
    }

}