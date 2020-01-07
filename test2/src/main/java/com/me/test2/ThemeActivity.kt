package com.me.test2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.me.pluginlib.activity.PluginAppCompatActivity
import kotlinx.android.synthetic.main.activity_theme.*

class ThemeActivity : PluginAppCompatActivity() {

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
    }
}
