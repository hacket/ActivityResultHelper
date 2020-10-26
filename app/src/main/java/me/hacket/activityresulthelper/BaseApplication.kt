package me.hacket.activityresulthelper

import android.app.Application
import me.hacket.library.ActivityResultHelper

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ActivityResultHelper.getInstance().init(this)
    }
}