package com.thegergo02.minkreta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: Controller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = Controller(this, ApiHandler(this))

        login_btt.setOnClickListener {
            controller.getTokens(username_et.text.toString(), password_et.text.toString(), inst_code_et.text.toString())
        }
    }

    override fun setApiLinkText(link: String) {
        api_tv.text = link
    }

    override fun setInstitutes(institutes: JSONArray) {
        api_tv.text = institutes[0].toString()
    }

    override fun setTokens(tokens: JSONObject) {
        access_tv.text = tokens["access_token"].toString()
        refresh_tv.text = tokens["refresh_token"].toString()
    }
}
