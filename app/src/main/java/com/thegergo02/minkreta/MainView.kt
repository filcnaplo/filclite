package com.thegergo02.minkreta

import org.json.JSONArray
import org.json.JSONObject

interface MainView {
    fun setApiLinkText(link: String)
    fun setInstitutes(institutes: JSONArray)
    fun setTokens(tokens: JSONObject)
}