package com.papero.gituser.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.papero.gituser.R
import com.papero.gituser.presentation.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}