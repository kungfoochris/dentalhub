package com.abhiyantrik.dentalhub

import android.content.Context
import android.util.Log
import com.abhiyantrik.dentalhub.entities.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import timber.log.Timber

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()

        if (BuildConfig.DEBUG) {
            AndroidObjectBrowser(boxStore).start(context.applicationContext)
            Timber.d(
                "Using ObjectBox ${BoxStore.getVersion()} (${BoxStore.getVersionNative()})"
            )
        }
    }

}