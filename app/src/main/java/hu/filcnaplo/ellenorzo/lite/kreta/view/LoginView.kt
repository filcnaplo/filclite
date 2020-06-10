package hu.filcnaplo.ellenorzo.lite.view

import hu.filcnaplo.ellenorzo.lite.kreta.data.Institute

interface LoginView {
    fun setInstitutes(institutes: List<Institute>)
    fun setTokens(tokens: Map<String, String>)
    fun hideProgress()
    fun showProgress()
    fun displayError(error: String)
}