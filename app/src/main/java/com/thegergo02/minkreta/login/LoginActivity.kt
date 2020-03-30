package com.thegergo02.minkreta.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.MainActivity
import com.thegergo02.minkreta.controller.LoginController
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), LoginView {
    private lateinit var controller: LoginController
    private var institutesWithCode = mutableMapOf<String, String>()
    private var stringInstitutes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        controller = LoginController(this, ApiHandler(this))
        controller.getInstitutes()
        inst_code_s?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var instituteCode = institutesWithCode.get(inst_code_s.selectedItem.toString())
                inst_code_tt.text = instituteCode
            }
        }
        login_btt.setOnClickListener {
            var instituteCode = institutesWithCode.get(inst_code_s.selectedItem.toString()).toString()
            var userName = username_et.text.toString()
            var password = password_et.text.toString()
            controller.getTokens(userName, password, instituteCode)
        }
    }

    override fun setInstitutes(institutes: JSONArray) {
        for (i in 0 until institutes.length()) {
            val institute = JSONObject(institutes[i].toString())
            institutesWithCode.put(institute["Name"].toString(), institute["InstituteCode"].toString())
            stringInstitutes.add(institute["Name"].toString())

        }
        stringInstitutes.sortBy({it})
        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stringInstitutes)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        inst_code_s.adapter = adapter
    }

    override fun setTokens(tokens: JSONObject) {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("access_token", tokens["access_token"].toString())
        mainIntent.putExtra("refresh_token", tokens["refresh_token"].toString())
        mainIntent.putExtra("institute_code", institutesWithCode.get(inst_code_s.selectedItem.toString()))
        startActivity(mainIntent)
    }
}
