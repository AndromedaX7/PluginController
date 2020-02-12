package com.me.host

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import android.util.Log
import com.me.hostlib.Plugins


class TestApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Plugins.init(this)
        Log.e("attached" ,"App attached")
    }

    override fun onCreate() {
        super.onCreate()
//        Plugins.getInstance().installOrLoad(this,"nativelib-debug.apk")
//        Plugins.getInstance().installOrLoad(this,"cp-2.9.0.apk")
        Log.e("process", _getProcessName())
        Log.e("onCreate","start")
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun isMainProcess(context: Context?): Boolean {
        return isPidOfProcessName(context!!, Process.myPid(), getMainProcessName(context))
    }

    fun isPidOfProcessName(
        context: Context,
        pid: Int,
        p_name: String?
    ): Boolean {
        if (p_name == null) return false
        var isMain = false
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //遍历所有进程
        for (process in am.runningAppProcesses) {
            if (process.pid == pid) { //进程ID相同时判断该进程名是否一致
                if (process.processName == p_name) {
                    isMain = true
                }
                break
            }
        }
        return isMain
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getMainProcessName(context: Context): String? {
        return context.packageManager.getApplicationInfo(context.packageName, 0)
            .processName
    }

    fun _getProcessName(): String {
        val myPid = Process.myPid()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = am.runningAppProcesses
        for (proc in runningAppProcesses) {
            if (proc.pid == myPid) {
                return proc.processName
            }
        }
        return ""
    }

}