package com.thegergo02.minkreta.view

import org.json.JSONArray
import org.json.JSONObject

interface LoginView {
    fun setInstitutes(institutes: JSONArray)
    fun setTokens(tokens: JSONObject)
    fun hideProgress()
    fun showProgress()
    fun displayError(error: String)
}