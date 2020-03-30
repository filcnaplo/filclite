package com.thegergo02.minkreta.controller

import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.data.Student
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class ApiLinkHelper(private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    private var currentApiLink = ""
    init {
        getApiLink()
    }

    fun getCurrentApiLink() : String {
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

    override fun onApiLinkSuccess(str: String) {
        currentApiLink = str
    }
    override fun onApiLinkError(error: String) {
        currentApiLink = "https://kretaglobalmobileapi.ekreta.hu"
    }

    override fun onInstitutesSuccess(str: JSONArray) {}
    override fun onInstitutesError(str: String) {}
    override fun onTokensSuccess(tokens: String) {}
    override fun onTokensError(error: String) {}
    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {}
    override fun onStudentError(error: String) {}
}