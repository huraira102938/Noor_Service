package com.danish.noorservice

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoorServiceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initCloudinary()
    }

    private fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "du3xdu8ne"
        )
        MediaManager.init(this, config)
    }
}