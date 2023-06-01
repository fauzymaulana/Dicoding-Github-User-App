package com.papero.gituser

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration

class CoreApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "LifecycleCallbacks"
    }

    private val realmSchemaVersion = 1L
    private val realmDbPassword = "66b167524b39b89d4a7f1ef4b6ea7e40b0764e5026bfefdae6254bcf5f0d332c"
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("com.papero.gituser.realmdb")
            .schemaVersion(realmSchemaVersion)
            .encryptionKey(realmDbPassword.toByteArray())
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)

        registerActivityLifecycleCallbacks(this)
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Log.d(TAG, "onActivityCreated at ${p0.localClassName}")
    }

    override fun onActivityStarted(p0: Activity) {
        Log.d(TAG, "onActivityStarted at ${p0.localClassName}")
    }

    override fun onActivityResumed(p0: Activity) {
        Log.d(TAG, "onActivityResumed at ${p0.localClassName}")
    }

    override fun onActivityPaused(p0: Activity) {
        Log.d(TAG, "onActivityPaused at ${p0.localClassName}")
    }

    override fun onActivityStopped(p0: Activity) {
        Log.d(TAG, "onActivityStopped at ${p0.localClassName}")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState at ${p0.localClassName}")
    }

    override fun onActivityDestroyed(p0: Activity) {
        Log.d(TAG, "onActivityDestroyed at ${p0.localClassName}")
    }
}
