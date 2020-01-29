package com.me.host

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.me.hostlib.Host
import com.me.hostlib.plugin.ActivitySlotManager
import com.me.hostlib.plugin.ProviderService
import com.me.hostlib.plugin.SlotManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testProvider.setOnClickListener {
//            Host.getInstance().startService(this,Intent().setComponent(ComponentName(this.packageName,"com.me.test2.MyService")))
//            Host.getInstance().startActivity(this, Intent().setComponent(ComponentName(this.packageName,"com.me.nativelib.MainActivity")))
            ProviderService.getInstance(this).query(Uri.parse("content://i.test.o2.provider"),null,null,null,null)
//            contentResolver.call(Uri.parse("content://i.app.o2.provider"),"test","args",null);
        }
        test2.setOnClickListener {
            Host.getInstance().startActivity(this, Intent().setComponent(ComponentName(this.packageName,"com.me.test2.ThemeActivity")))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        Host.getInstance().stopService(this,Intent().setComponent(ComponentName(this.packageName,"com.me.test2.MyService")))
    }
}
