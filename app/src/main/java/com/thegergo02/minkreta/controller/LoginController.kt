package com.thegergo02.minkreta.controller

import com.thegergo02.minkreta.R
import com.android.volley.*
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.misc.Strings
import com.thegergo02.minkreta.view.LoginView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class LoginController(private var loginView: LoginView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    private val apiLinkHelper = ApiLinkHelper(apiHandler)

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

    override fun onInstitutesSuccess(institutes: JSONArray) {
        loginView?.setInstitutes(institutes)
        loginView?.hideProgress()
    }
    override fun onInstitutesError(error: VolleyError) {
        val errorString: String = when(error) {
            is ClientError -> Strings.get(R.string.client_error_inst)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }
        loginView?.displayError(errorString)
        loginView?.hideProgress()
    }

    override fun onTokensSuccess(tokens: String) {
        val tokensJson = JSONObject(tokens)
        loginView?.setTokens(tokensJson)
        loginView?.hideProgress()
    }
    override fun onTokensError(error: VolleyError) {
        val errorString: String = when(error) {
            is ClientError -> Strings.get(R.string.client_error_login)
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }
        loginView?.displayError(errorString)
        loginView?.hideProgress()
    }

    override fun onApiLinkSuccess(link: String) {}
    override fun onApiLinkError(error: String) {}
    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {}
    override fun onStudentError(error: VolleyError) {}
    override fun onTimetableSuccess(timetable: String) {}
    override fun onTimetableError(error: VolleyError) {}
}
