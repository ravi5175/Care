package com.petcaresystem

import android.app.Application
import com.bugfender.sdk.Bugfender

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugfender.init(this, "ZpR8VeYXAvgE8AVDSDCBaI1adIcvCS2C", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()
    }
}