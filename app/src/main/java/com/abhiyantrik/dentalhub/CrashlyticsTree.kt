package com.abhiyantrik.dentalhub

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree: Timber.Tree()
{
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        FirebaseCrashlytics.getInstance().log(String.format("%d %s %s", priority , tag, message));
    }
}