package com.example.calculator

import android.app.Application
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(timber.log.Timber.DebugTree())
        }
    }
}