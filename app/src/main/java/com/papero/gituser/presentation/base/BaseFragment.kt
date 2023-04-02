package com.papero.gituser.presentation.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment: Fragment() {

    fun isVisibleView(visibility: Int, vararg views: View) {
        for (view in views) {
            view.visibility = visibility
        }
    }

    fun hideKeyPad(rootView: ViewGroup?){
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(rootView?.windowToken, 0)
    }

    fun showKeypad(rootView: ViewGroup?){
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(rootView, InputMethodManager.SHOW_FORCED)
    }

    fun showSnackbar(view: View, message: String?) {
        val snackMessage = message ?: "Something went wrong"
        Snackbar.make(view, snackMessage, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBarwithAction(
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