package com.thegergo02.minkreta.controller

import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.view.LoginView
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class LoginController(private var loginView: LoginView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    val apiLinkHelper = ApiLinkHelper(apiHandler)

    fun getInstitutes() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getInstitutes(parentListener, apiLinkHelper.getCurrentApiLink())
        }
    }

    fun getTokens(userName: String, password: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTokens(parentListener, userName, password, instituteCode)
        }
    }

    fun onDestroy() {
        loginView = null
    }

    override fun onInstitutesSuccess(institutes: JSONArray) {
        loginView?.setInstitutes(institutes)
    }
    override fun onInstitutesError(error: String) {
    }

    override fun onTokensSuccess(tokens: String) {
        val tokensJson = JSONObject(tokens)
        loginView?.setTokens(tokensJson)
    }
    override fun onTokensError(error: String) {
    }

    override fun onApiLinkSuccess(link: String) {}
    override fun onApiLinkError(error: String) {}
    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {}
    override fun onStudentError(error: String) {}
}
