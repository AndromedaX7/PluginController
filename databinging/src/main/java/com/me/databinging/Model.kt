package com.me.databinging

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
        v as Button
        v.text = text
    }
}