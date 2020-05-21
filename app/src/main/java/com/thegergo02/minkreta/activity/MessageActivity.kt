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
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.controller.MessageController
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.Receiver
import com.thegergo02.minkreta.ui.UIHelper
import com.thegergo02.minkreta.view.LoginView
import com.thegergo02.minkreta.view.MessageView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity(), MessageView {
    private lateinit var controller: MessageController
    private var receivers = mutableListOf<Receiver>()
    private var attachments = mutableListOf<Attachment>()
    private var replyId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val sharedPref = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return
        val accessToken = sharedPref.getString("accessToken", null)
        val refreshToken = sharedPref.getString("refreshToken", null)
        val instituteCode = sharedPref.getString("instituteCode", null)
        if (accessToken != null && refreshToken != null && instituteCode != null) {
            controller = MessageController(this, this, accessToken, refreshToken, instituteCode)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
        message_btt.setOnClickListener {
            controller.sendMessage(receivers, attachments, subject_mea.text.toString(), message_mea.text.toString(), replyId)
        }
    }

    override fun generateReceiverList(receivers: List<Receiver>) {

    }

    override fun displayError(error: String) {
        UIHelper.displayError(this, message_cl, error)
    }

    override fun displaySuccess(success: String) {
        UIHelper.displaySuccess(this, message_cl, success)
    }

    override fun backToMain() {
        finish()
    }
}
