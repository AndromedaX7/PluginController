package com.me.host

import android.content.ComponentName
import android.content.Intent
import android.net.Uri.parse
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.hostlib.Host
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testProvider.setOnClickListener {

            val parse =
                parse("content://com.yhkj.glassapp.photo.provider/external_files/image/1581304461610.jpg")
            Log.e("scheme ", parse.scheme)
            Log.e("auth ", parse.authority)
            Log.e("host ", parse.host)
            Log.e("query ", parse?.query ?: "")
            Log.e("path ", parse.path)

            val list = parse.pathSegments
            for (l in list)
                Log.e("pathSegments ", l ?: "")
            val lastPathSegment = parse.lastPathSegment
            Log.e("lastPathSegment ", lastPathSegment ?: "")
            //            Host.getInstance().startService(this,Intent().setComponent(ComponentName(this.packageName,"com.me.test2.MyService")))
            Host.getInstance().startActivity(this, Intent("action.image"))
//            Host.getInstance().startActivity(this, Intent().setComponent(ComponentName(this.packageName,"com.me.nativelib.MainActivity")))
//            Host.getInstance().startActivity(this, Intent().setComponent(ComponentName(this.packageName,"com.yhkj.glasshelper.MainActivity")))
//            Host.getInstance().startActivity(this, Intent().setComponent(ComponentName(this.packageName,"com.yhkj.glasshelper.activities.HelperMainActivity")))
//            ProviderService.getInstance(this).query(Uri.parse("content://i.test.o2.provider"),null,null,null,null)
//            contentResolver.call(Uri.parse("content://i.app.o2.provider"),"test","args",null);
        }
        test2.setOnClickListener {
            Host.getInstance().startActivity(
                this,
                Intent().setComponent(ComponentName(this.packageName, "com.me.test2.ThemeActivity"))
            )
        }
    }

}
