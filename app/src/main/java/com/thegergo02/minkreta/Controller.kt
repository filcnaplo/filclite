package com.thegergo02.minkreta

import android.util.Log
import com.thegergo02.minkreta.data.Student
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class Controller(private var mainView: MainView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    lateinit var student: Student
    private lateinit var currentApiLink: String
    init {
        getApiLink()
    }

    private fun getCurrentApiLink() : String {
        while (currentApiLink == "") {
            continue
        }
        return currentApiLink
    }

    fun getApiLink(apiType : ApiHandler.ApiType = ApiHandler.ApiType.PROD) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getApiLink(parentListener, apiType)
        }
    }

    fun getInstitutes() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getInstitutes(parentListener, getCurrentApiLink())
        }
    }

    fun getTokens(userName: String, password: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTokens(parentListener, getCurrentApiLink(), userName, password, instituteCode)
        }
    }

    fun onDestroy() {
        mainView = null
    }

    override fun onApiLinkSuccess(link: String) {
        currentApiLink = link
        mainView?.setApiLinkText(link)
    }
    override fun onApiLinkError(error: String) {
        mainView?.setApiLinkText(error)
    }

    override fun onInstitutesSuccess(institutes: JSONArray) {
        mainView?.setInstitutes(institutes)
    }
    override fun onInstitutesError(str: String) {
        mainView?.setApiLinkText(str)
    }

    override fun onTokensSuccess(tokens: String) {
        val tokensJson = JSONObject(tokens)
        mainView?.setTokens(tokensJson)
    }
    override fun onTokensError(str: String) {
        mainView?.setApiLinkText(str)
    }
}
