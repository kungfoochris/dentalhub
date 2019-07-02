package com.example.dentalhub

import android.content.Context
import android.util.Log
import com.example.dentalhub.entities.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()

        if (BuildConfig.DEBUG) {
            Log.d("ObjectBox", "Using ObjectBox ${BoxStore.getVersion()} (${BoxStore.getVersionNative()})")
        }
    }

}