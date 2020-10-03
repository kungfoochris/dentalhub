package com.abhiyantrik.dentalhub.utils

import com.abhiyantrik.dentalhub.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class FirebaseConfig {
    var mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    var configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder().build()
    var cacheExpiration: Long = 43200
    var editableDuration: Long = 21600

    init {
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun fetchEditableTime(): Long {
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener {
            mFirebaseRemoteConfig.fetchAndActivate()
        }

        editableDuration = mFirebaseRemoteConfig.getLong("editable_duration")
        return editableDuration
    }
}