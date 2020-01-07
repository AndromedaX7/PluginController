package com.me.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.me.pluginlib.activity.PluginAppCompatActivity

class ImageActivity : PluginAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
    }
}
