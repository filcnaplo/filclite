package com.thegergo02.minkreta

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.thegergo02.minkreta.data.Student
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.Charset

class ApiHandler(ctx: Context) {
    enum class ApiType(val type: String) {
        PROD("PROD"),
        DEV("DEV"),
        UAT("UAT"),
        TEST("TEST")
    }

    interface OnFinishedResult {
        fun onApiLinkSuccess(link: String)
        fun onApiLinkError(error: String)

        fun onInstitutesSuccess(institutes: JSONArray)
        fun onInstitutesError(error: VolleyError)

        fun onTokensSuccess(tokens: String)
        fun onTokensError(error: VolleyError)

        fun onStudentSuccess(student: String, accessToken: String, refreshToken: String)
        fun onStudentError(error: VolleyError)
    }

    private val queue = Volley.newRequestQueue(ctx)
    private val API_KEY = "7856d350-1fda-45f5-822d-e1a2f3f1acf0"
    private val CLIENT_ID = "919e0c1c-76a2-4646-a2fb-7085bbbf3c56"
    private val USER_AGENT = "Kreta.Ellenorzo"

    private val API_HOLDER_LINK = "https://kretamobile.blob.core.windows.net/configuration/ConfigurationDescriptor.json"
    suspend fun getApiLink(listener: OnFinishedResult, apiType : ApiType = ApiType.PROD) {
        val apiLinksQuery = JsonObjectRequest(Request.Method.GET, API_HOLDER_LINK, null,
                Response.Listener { response ->
                    listener.onApiLinkSuccess(response["GlobalMobileApiUrl${apiType.toString()}"].toString())
                },
                Response.ErrorListener { error ->
                    listener.onApiLinkError(error.toString())
                }
            )
        queue.add(apiLinksQuery)
    }

    suspend fun getInstitutes(listener: OnFinishedResult, apiLink: String) {
        val institutesQuery = object : JsonArrayRequest(Request.Method.GET, "${apiLink}/api/v1/Institute", null,
            Response.Listener { response ->
                listener.onInstitutesSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onInstitutesError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf<String, String>("apiKey" to API_KEY)
        }
        queue.add(institutesQuery)
    }

    suspend fun getTokens(listener: OnFinishedResult, userName: String, password: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(Request.Method.POST, "https://$instituteCode.e-kreta.hu/idp/api/v1/Token",
            Response.Listener { response ->
                listener.onTokensSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onTokensError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf<String, String>("apiKey" to API_KEY,
                "Accept" to "application/json",
                "User-Agent" to USER_AGENT)
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
            override fun getBody(): ByteArray = "password=$password&institute_code=$instituteCode&grant_type=password&client_id=$CLIENT_ID&userName=$userName".toByteArray()
        }
        queue.add(tokensQuery)
    }

    suspend fun getStudent(listener: OnFinishedResult, accessToken: String, refreshToken: String, instituteCode: String) {
        val studentQuery = object : StringRequest(Request.Method.GET, "https://$instituteCode.e-kreta.hu/mapi/api/v1/StudentAmi",
            Response.Listener { response ->
                listener.onStudentSuccess(response, accessToken, refreshToken)
            },
            Response.ErrorListener { error ->
                listener.onStudentError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf<String, String>(
                "Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to USER_AGENT)
        }
        queue.add(studentQuery)
    }
}