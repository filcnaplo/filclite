package hu.filcnaplo.ellenorzo.lite.controller

import android.content.Context
import hu.filcnaplo.ellenorzo.lite.kreta.KretaError
import hu.filcnaplo.ellenorzo.lite.kreta.KretaRequests
import hu.filcnaplo.ellenorzo.lite.kreta.data.Institute
import hu.filcnaplo.ellenorzo.lite.view.LoginView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginController(ctx: Context, private val loginView: LoginView?)
    : KretaRequests.OnInstitutesResult, KretaRequests.OnTokensResult {

    private val apiHandler = KretaRequests(ctx)

    fun getInstitutes() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getInstitutes(parentListener)
        }
    }

    fun getTokens(userName: String, password: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTokens(parentListener, userName, password, instituteCode)
        }
    }

    override fun onInstitutesSuccess(institutes: List<Institute>) {
        loginView?.setInstitutes(institutes)
        loginView?.hideProgress()
    }
    override fun onInstitutesError(error: KretaError) {
        loginView?.displayError(error.errorString)
        loginView?.hideProgress()
    }

    override fun onTokensSuccess(tokens: Map<String, String>) {
        loginView?.setTokens(tokens)
        loginView?.hideProgress()
    }
    override fun onTokensError(error: KretaError) {
        loginView?.displayError(error.errorString)
        loginView?.hideProgress()
    }
}
