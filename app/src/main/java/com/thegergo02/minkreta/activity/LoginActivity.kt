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

class LoginActivity : AppCompatActivity(), LoginView {
    private lateinit var controller: LoginController
    private var instituteNames = mutableMapOf<String, Institute>()
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

        controller = LoginController(this, this)
        controller.getInstitutes()
        showProgress()
        inst_code_s?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val institute = instituteNames[inst_code_s.selectedItem.toString()]
                inst_code_tt.text = institute.toString()
            }
        }
        login_btt.setOnClickListener {
            val institute = instituteNames[inst_code_s.selectedItem.toString()]
            val userName = username_et.text.toString()
            val password = password_et.text.toString()
            if (institute != null) {
                controller.getTokens(userName, password, institute.code)
            }
            showProgress()
        }
    }

    override fun setInstitutes(institutes: List<Institute>) {
        for (institute in institutes) {
            instituteNames[institute.name] = institute
            stringInstitutes.add(institute.name)

        }
        stringInstitutes.sortBy{it}
        val adapter = ArrayAdapter(this, R.layout.login_spinner_item, stringInstitutes)
        adapter.setDropDownViewResource(R.layout.login_spinner_dropdown_item)
        inst_code_s.adapter = adapter
    }
    override fun setTokens(tokens: Map<String, String>) {
        val mainIntent = Intent(this, MainActivity::class.java)
        val sharedPref = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            val institute = instituteNames[inst_code_s.selectedItem.toString()]
            if (institute != null) {
                putString("accessToken", tokens["access_token"])
                putString("refreshToken", tokens["refresh_token"])
                putString("instituteUrl", institute.url)
                putString("instituteCode", institute.toString())
            }
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
