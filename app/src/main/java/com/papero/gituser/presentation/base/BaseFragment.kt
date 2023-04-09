package com.papero.gituser.presentation.base

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment: Fragment() {

    fun isVisibleView(visibility: Int, vararg views: View) {
        for (view in views) {
            view.visibility = visibility
        }
    }

    fun showSnackBarWithAction(
        color: Int?,
        message: String?,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackMessage = message ?: "Something went wrong"
        val snackbar = Snackbar.make(requireView(), snackMessage, Snackbar.LENGTH_LONG)
            .setBackgroundTint(
                resources.getColor(
                    color ?: com.google.android.material.R.color.m3_ref_palette_neutral20
                )
            )
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(requireView())
            }.show()
        } else {
            snackbar.show()
        }
    }

}