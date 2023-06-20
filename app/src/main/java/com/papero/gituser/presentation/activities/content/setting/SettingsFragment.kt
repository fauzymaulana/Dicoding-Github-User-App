package com.papero.gituser.presentation.activities.content.setting

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papero.gituser.R
import com.papero.gituser.databinding.FragmentSettingsBinding
import com.papero.gituser.presentation.base.BaseFragment
import com.papero.gituser.utilities.datastore.SettingPrefs
import com.papero.gituser.utilities.datastore.dataStore

class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var bottomNav: BottomNavigationView? = null
    private var viewModel: SettingsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = activity?.findViewById(R.id.bottom_nav)
        val pref = SettingPrefs.getInstance(requireActivity().application.dataStore)
        viewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(SettingsViewModel::class.java)
        switchTheme()
        initListeners()
    }

    private fun initListeners() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked: Boolean ->
            viewModel?.saveTheme(isChecked)
        }
    }

    private fun switchTheme() {
        viewModel?.getTheme()?.observe(viewLifecycleOwner){isDarkModeActive: Boolean ->
            if (isDarkModeActive){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNav?.visibility = View.GONE
    }
}

class SettingViewModelFactory(
    private val settingPrefs: SettingPrefs
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(settingPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}