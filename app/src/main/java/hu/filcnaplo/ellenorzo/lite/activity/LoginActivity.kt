package hu.filcnaplo.ellenorzo.lite.activity

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import hu.filcnaplo.ellenorzo.lite.R
import hu.filcnaplo.ellenorzo.lite.controller.LoginController
import hu.filcnaplo.ellenorzo.lite.kreta.data.Institute
import hu.filcnaplo.ellenorzo.lite.ui.ThemeHelper
import hu.filcnaplo.ellenorzo.lite.ui.UIHelper
import hu.filcnaplo.ellenorzo.lite.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AccountAuthenticatorActivity(), LoginView {
    private lateinit var controller: LoginController
    private lateinit var themeHelper: ThemeHelper

    private var instituteNames = mutableMapOf<String, Institute>()
    private var originalStringInstitutes = mutableListOf<String>()
    private var stringInstitutes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeHelper = ThemeHelper(this)
        themeHelper.setCurrentTheme()
        setContentView(R.layout.activity_login)

        controller = LoginController(this, this)
        controller.getInstitutes()
        showProgress()
        inst_code_tt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newStringInstitutes = mutableListOf<String>()
                originalStringInstitutes.forEach {
                    if (it.contains(s.toString(), true) || instituteNames[it]?.code?.contains(s.toString(), true) == true)
                        newStringInstitutes.add(it)
                }
                stringInstitutes = newStringInstitutes
                refreshInstitutesSpinner()
            }
        })
        login_btt.setOnClickListener {
            val institute = instituteNames[inst_code_s.selectedItem.toString()]
            inst_code_tt.setText(institute.toString())
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
            originalStringInstitutes.add(institute.name)

        }
        originalStringInstitutes.sortBy{it}
        stringInstitutes = originalStringInstitutes
        refreshInstitutesSpinner()
    }

    private fun refreshInstitutesSpinner() {
        val spinnerLayouts = themeHelper.getResourcesFromAttributes(listOf(R.attr.loginSpinnerItemLayout, R.attr.loginSpinnerDropdownItemLayout))
        val adapter = ArrayAdapter(this, spinnerLayouts[0], stringInstitutes)
        adapter.setDropDownViewResource(spinnerLayouts[1])
        inst_code_s.adapter = adapter
    }

    override fun setTokens(tokens: Map<String, String>) {
        val userName = username_et.text.toString()
        val password = password_et.text.toString()
        val account = Account(userName, getString(R.string.kreta_account_id))
        val accountManager = AccountManager.get(this)
        if (intent.getBooleanExtra(getString(R.string.key_is_adding_new_account), false)) {
            val userData = Bundle()
            val institute = instituteNames[inst_code_s.selectedItem.toString()]
            userData.putString(getString(R.string.key_refresh_token), tokens["refresh_token"])
            userData.putString(getString(R.string.key_institute_code), institute.toString())
            accountManager.addAccountExplicitly(account, password, userData)
            accountManager.setAuthToken(account, getString(R.string.kreta_auth_type_full), tokens["access_token"])
        } else {
            accountManager.setPassword(account, password)
        }
        val res = Intent()
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName)
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.kreta_account_id))
        res.putExtra(AccountManager.KEY_AUTHTOKEN, tokens["access_token"])
        res.putExtra(getString(R.string.key_password), password)
        setAccountAuthenticatorResult(res.extras)
        setResult(Activity.RESULT_OK, res)
        finish()
    }

    override fun displayError(error: String) {
        UIHelper.displayError(this, login_cl, error)
    }

    override fun hideProgress() {
        login_loading_bar.visibility = View.GONE
    }
    override fun showProgress() {
        login_loading_bar.visibility = View.VISIBLE
    }
}
