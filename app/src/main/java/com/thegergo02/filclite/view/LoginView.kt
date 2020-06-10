package com.thegergo02.filclite.view

import com.thegergo02.filclite.kreta.data.Institute

interface LoginView {
    fun setInstitutes(institutes: List<Institute>)
    fun setTokens(tokens: Map<String, String>)
    fun hideProgress()
    fun showProgress()
    fun displayError(error: String)
}