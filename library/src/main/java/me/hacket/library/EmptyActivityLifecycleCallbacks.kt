package me.hacket.library

import android.app.Activity
import android.app.Application
import android.os.Bundle

open class EmptyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // empty
    }

    override fun onActivityStarted(activity: Activity) {
        // empty
    }

    override fun onActivityResumed(activity: Activity) {
        // empty
    }

    override fun onActivityPaused(activity: Activity) {
        // empty
    }

    override fun onActivityStopped(activity: Activity) {
        // empty
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // empty
    }

    override fun onActivityDestroyed(activity: Activity) {
        // empty
    }
}