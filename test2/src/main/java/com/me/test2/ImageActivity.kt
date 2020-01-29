package com.me.test2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.pluginlib.activity.PluginAppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        aa.setOnClickListener {
            Log.e("application", application.javaClass.name)
        }
    }
}
