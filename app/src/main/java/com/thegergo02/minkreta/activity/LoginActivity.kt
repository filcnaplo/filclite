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
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.LoginController
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

        val sharedPref = getSharedPreferences("com.thegergo02.minkreta.auth", Context.MODE_PRIVATE) ?: return
        if (sharedPref.getString("accessToken", null) != null) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        controller = LoginController(this, ApiHandler(this))
        controller.getInstitutes()
        showProgress()
        inst_code_s?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val instituteCode = institutesWithCode[inst_code_s.selectedItem.toString()]
                inst_code_tt.text = instituteCode
            }
        }
        login_btt.setOnClickListener {
            val instituteCode = institutesWithCode[inst_code_s.selectedItem.toString()].toString()
            val userName = username_et.text.toString()
            val password = password_et.text.toString()
            controller.getTokens(userName, password, instituteCode)
            showProgress()
        }
    }

    override fun setInstitutes(institutes: JSONArray) {
        for (i in 0 until institutes.length()) {
            val institute = JSONObject(institutes[i].toString())
            institutesWithCode[institute["Name"].toString()] = institute["InstituteCode"].toString()
            stringInstitutes.add(institute["Name"].toString())

        }
        stringInstitutes.sortBy{it}
        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stringInstitutes)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        inst_code_s.adapter = adapter
    }
    override fun setTokens(tokens: JSONObject) {
        val mainIntent = Intent(this, MainActivity::class.java)
        val sharedPref = getSharedPreferences("com.thegergo02.minkreta.auth", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("accessToken", tokens["access_token"].toString())
            putString("refreshToken", tokens["refresh_token"].toString())
            putString("instituteCode", institutesWithCode[inst_code_s.selectedItem.toString()])
            commit()
        }
        startActivity(mainIntent)
        finish()
    }

    override fun displayError(error: String) {
        val errorSnack = Snackbar.make(login_cl, error, Snackbar.LENGTH_LONG)
        errorSnack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError))
        errorSnack.show()
    }

    override fun hideProgress() {
        login_loading_bar.visibility = View.GONE
    }
    override fun showProgress() {
        login_loading_bar.visibility = View.VISIBLE
    }
}
