package com.thegergo02.minkreta.controller

import com.android.volley.*
import com.android.volley.toolbox.Volley
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

    fun onDestroy() {
        loginView = null
    }

    override fun onInstitutesSuccess(institutes: JSONArray) {
        loginView?.setInstitutes(institutes)
        loginView?.hideProgress()
    }
    override fun onInstitutesError(error: VolleyError) {
        var errorString: String
        when(error) {
            is ClientError -> errorString = "Couldn't get institutes. (ClientError)"
            is NoConnectionError -> errorString = "Can't get institutes without an internet connection."
            else -> errorString = error.toString()
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
        var errorString: String
        when(error) {
            is ClientError -> errorString = "Maybe you left a field empty? (ClientError)"
            is AuthFailureError -> errorString = "Wrong credetinals! (AuthFailureError)"
            is TimeoutError -> errorString = "The KRETA server took too long to respond. (TimeoutError)"
            is NetworkError -> errorString = "Maybe the request got interrupted? (NetworkError) (${error.message})"
            is NoConnectionError -> errorString = "Can't login without an internet connection."
            else -> errorString = error.toString()
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
