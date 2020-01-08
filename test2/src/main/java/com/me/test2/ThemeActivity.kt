package com.me.test2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.me.pluginlib.activity.PluginAppCompatActivity
import kotlinx.android.synthetic.main.activity_theme.*

class ThemeActivity : PluginAppCompatActivity(),ServiceConnection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        c.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        a.setOnClickListener {
            startService(Intent(this,MyService2::class.java).putExtra("start",1))
        }
        b.setOnClickListener {
            stopService(Intent(this,MyService2::class.java))
        }
        d.setOnClickListener {
            bindService(Intent(this,MyService2::class.java),this, Context.BIND_AUTO_CREATE)
        }
        e.setOnClickListener {
            unbindService(this)
        }
        f.setOnClickListener {
            sendBroadcast(Intent("com.receiver"))
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.e("unbindService","${name?.className}")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.e("bindService","${name?.className}")
        IMyService2.Stub.asInterface(service)
            .connect("Hello")
    }


}
