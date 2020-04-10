package com.thegergo02.minkreta.controller

import com.android.volley.*
import com.thegergo02.minkreta.ApiHandler
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
            is ClientError -> "There was a problem while getting institutes!"
            is NoConnectionError -> "There is no internet connection!"
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
            is ClientError -> "You left a field empty!"
            is AuthFailureError -> "Wrong credetinals!"
            is TimeoutError -> "The KRETA servers took too long to respond!"
            is NetworkError -> "The request abruptly closed!"
            is NoConnectionError -> "There is no internet connection!"
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
    override fun onMessageListSuccess(messageListString: String) {}
    override fun onMessageListError(error: VolleyError) {}
    override fun onMessageSuccess(messageString: String) {}
    override fun onMessageError(error: VolleyError) {}
    override fun onRefreshTokensSuccess(tokens: String) {}
    override fun onRefreshTokensError(error: VolleyError) {}
    override fun onTestsSuccess(tests: String) {}
    override fun onTestsError(error: VolleyError) {}
    override fun onStudentHomeworkSuccess(homeworkString: String, isLast: Boolean) {}
    override fun onStudentHomeworkError(error: VolleyError) {}
}
