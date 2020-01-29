package com.me.databinging

import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class Model : BaseObservable() {
    @get:Bindable
    var text=""
    set(value) {
        field=value
        notifyPropertyChanged(BR.text)
    }

    fun setTextShow(v:View){
       v.context.sendBroadcast(Intent("test.receiver").setPackage(v.context.packageName))
    }
}