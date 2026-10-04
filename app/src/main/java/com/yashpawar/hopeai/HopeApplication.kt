package com.yashpawar.hopeai

import android.app.Application
import com.yashpawar.hopeai.notifications.NotificationCenter

class HopeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationCenter.createChannel(this)
    }
}

