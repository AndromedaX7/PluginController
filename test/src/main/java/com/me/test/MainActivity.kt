package com.me.test

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.me.pluginlib.activity.PluginAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : PluginAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testProvider.setOnClickListener {
            //            contentResolver.query(Uri.parse("content://com.me.host.test.provider"),null,null,null,null)
//            contentResolver.call(Uri.parse("content://com.me.host.test.provider"),"","", null)
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show()
            startActivity(Intent(this,SettingsActivity::class.java))
        }
    }
}
