package com.papero.gituser.presentation.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava2.RxDataStore
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.papero.gituser.R
import com.papero.gituser.databinding.ActivityMainBinding
import com.papero.gituser.presentation.base.BaseActivity
import io.reactivex.Flowable

class MainActivity : BaseActivity () {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

//        val dataStore: RxDataStore<Preferences> = RxPreferenceDataStoreBuilder(this,  name= "settings").build()
//        val EXAMPLE_COUNTER: Preferences.Key<Int> = intPreferencesKey("example_counter")
////        get value from preferences data store
//        val exampleCounterFlow: Flowable<Int> = dataStore.data().map { it[EXAMPLE_COUNTER] ?: 0 }
//
//
        setupBottomView()
    }


    private fun setupBottomView(){
        navHostFragment= supportFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding?.bottomNav?.setupWithNavController(navController)

    }
}