package com.papero.gituser.presentation.base

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity: AppCompatActivity() {

    fun showSnackbar(message: String?) {
        val snackMessage = message ?: "Something went wrong"
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, snackMessage, Snackbar.LENGTH_LONG).show()
    }

    fun showToast(message: String?) {
        val toastMessage = message ?: "Something went wrong"
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT)
            .show()
    }

    @SuppressLint("PrivateResource")
    fun showSnackBarwithAction(
        color: Int?,
        message: String?,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val rootView = findViewById<View>(android.R.id.content)
        val snackMessage = message ?: "Something went wrong"
        val snackbar = Snackbar.make(rootView, snackMessage, Snackbar.LENGTH_LONG)
            .setBackgroundTint(
                resources.getColor(
                    color ?: com.google.android.material.R.color.m3_ref_palette_neutral20
                )
            )
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(rootView)
            }.show()
        } else {
            snackbar.show()
        }
    }

}