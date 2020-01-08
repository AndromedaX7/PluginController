package com.me.test2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.me.pluginlib.PluginManager
import com.me.pluginlib.activity.PluginAppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : PluginAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        aa.setOnClickListener {
            Log.e("application", application.javaClass.name)
        }
    }
}
