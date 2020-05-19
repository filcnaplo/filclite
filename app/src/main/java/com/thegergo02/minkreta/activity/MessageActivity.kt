package com.thegergo02.minkreta.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.LoginController
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
    }

}
