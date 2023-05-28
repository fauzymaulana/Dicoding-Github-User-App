package com.papero.gituser.presentation.activities

import android.os.Bundle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava2.RxDataStore
import com.papero.gituser.R
import com.papero.gituser.presentation.base.BaseActivity
import io.reactivex.Flowable

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val dataStore: RxDataStore<Preferences> = RxPreferenceDataStoreBuilder(this,  name= "settings").build()
//        val EXAMPLE_COUNTER: Preferences.Key<Int> = intPreferencesKey("example_counter")
////        get value from preferences data store
//        val exampleCounterFlow: Flowable<Int> = dataStore.data().map { it[EXAMPLE_COUNTER] ?: 0 }
//
//

    }
}