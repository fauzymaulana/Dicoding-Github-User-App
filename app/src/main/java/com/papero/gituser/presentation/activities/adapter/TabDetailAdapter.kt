package com.papero.gituser.presentation.activities.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.papero.gituser.presentation.activities.content.detail.content.FollowFragment

class TabDetailAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    var username: String = ""

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = FollowFragment()

        fragment.arguments = Bundle().apply {
            putInt(FollowFragment.ARG_POSITION, position + 1)
            putString(FollowFragment.USERNAME_KEY, username)
        }

        return fragment
    }
}