package com.thegergo02.minkreta.controller

import com.android.volley.VolleyError
import com.thegergo02.minkreta.ApiHandler
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

    private fun getApiLink(apiType : ApiHandler.ApiType = ApiHandler.ApiType.PROD) {
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
    override fun onInstitutesError(str: VolleyError) {}
    override fun onTokensSuccess(tokens: String) {}
    override fun onTokensError(error: VolleyError) {}
    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {}
    override fun onStudentError(error: VolleyError) {}
    override fun onTimetableSuccess(timetable: String) {}
    override fun onTimetableError(error: VolleyError) {}

}