package com.thegergo02.minkreta.controller

import com.android.volley.VolleyError
import com.thegergo02.minkreta.kreta.KretaError
import com.thegergo02.minkreta.kreta.KretaRequests
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class ApiLinkHelper(private val apiHandler: KretaRequests)
    : KretaRequests.OnApiLinkResult {

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

    private fun getApiLink(apiType : KretaRequests.ApiType = KretaRequests.ApiType.PROD) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getApiLink(parentListener, apiType)
        }
    }

    override fun onApiLinkSuccess(link: String) {
        currentApiLink = link
    }
    override fun onApiLinkError(error: KretaError) {
        currentApiLink = "https://kretaglobalmobileapi2.ekreta.hu"
    }
}