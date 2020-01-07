package com.me.databinging

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.me.databinging.databinding.ActivityMainBinding
import com.me.pluginlib.activity.PluginAppCompatActivity

class MainActivity : PluginAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        b.m=Model();
    }
}
