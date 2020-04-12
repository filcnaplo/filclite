package com.thegergo02.minkreta.kreta

open class KretaError (
    val errorString: String
) {
    class VolleyError(errorString: String, val volleyError: com.android.volley.VolleyError): KretaError(errorString)
    class ParseError(errorString: String): KretaError(errorString)
}

