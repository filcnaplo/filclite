package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.kreta.data.Institute
import org.json.JSONObject

interface LoginView {
    fun setInstitutes(institutes: List<Institute>)
    fun setTokens(tokens: Map<String, String>)
    fun hideProgress()
    fun showProgress()
    fun displayError(error: String)
}