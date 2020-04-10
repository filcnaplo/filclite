package com.thegergo02.minkreta

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

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

        fun onRefreshTokensSuccess(tokens: String)
        fun onRefreshTokensError(error: VolleyError)

        fun onStudentSuccess(student: String, accessToken: String, refreshToken: String)
        fun onStudentError(error: VolleyError)

        fun onTimetableSuccess(timetableString: String)
        fun onTimetableError(error: VolleyError)

        fun onMessageListSuccess(messageListString: String)
        fun onMessageListError(error: VolleyError)

        fun onMessageSuccess(messageString: String)
        fun onMessageError(error: VolleyError)

        fun onTestsSuccess(testsString: String)
        fun onTestsError(error: VolleyError)

        fun onStudentHomeworkSuccess(homeworkString: String, isLast: Boolean)
        fun onStudentHomeworkError(error: VolleyError)
    }

    private val queue = Volley.newRequestQueue(ctx)
    private val API_KEY = "7856d350-1fda-45f5-822d-e1a2f3f1acf0"
    private val CLIENT_ID = "919e0c1c-76a2-4646-a2fb-7085bbbf3c56"
    private var userAgent = ""
    private val FALLBACK_USER_AGENT = "Kreta.Ellenorzo/2.9.10.2020031602 (Android; <codename> 0.0)"

   init {
       GlobalScope.launch {
           setUserAgent()
       }
   }

    private val USER_AGENT_LINK = "https://www.filcnaplo.hu/settings.json"
    private fun setUserAgent() {
        val userAgentQuery = JsonObjectRequest(Request.Method.GET, USER_AGENT_LINK, null,
            Response.Listener { response ->
                userAgent = response["KretaUserAgent"].toString().replace("(Android; <codename> 0.0)", "(Android; ${UUID.randomUUID()} ${(5..9)}.${2..9})")
            },
            Response.ErrorListener { error ->
                userAgent = FALLBACK_USER_AGENT.replace("(Android; <codename> 0.0)", "(Android; ${UUID.randomUUID()} ${(5..9)}.${2..9})")
            }
        )
        queue.add(userAgentQuery)
    }
    fun getUserAgent() : String {
        while (userAgent == "") {
            continue
        }
        return userAgent
    }

    private val API_HOLDER_LINK = "https://kretamobile.blob.core.windows.net/configuration/ConfigurationDescriptor.json"
    fun getApiLink(listener: OnFinishedResult, apiType : ApiType = ApiType.PROD) {
        val apiLinksQuery = JsonObjectRequest(Request.Method.GET, API_HOLDER_LINK, null,
                Response.Listener { response ->
                    listener.onApiLinkSuccess(response["GlobalMobileApiUrl$apiType"].toString())
                },
                Response.ErrorListener { error ->
                    listener.onApiLinkError(error.toString())
                }
            )
        queue.add(apiLinksQuery)
    }

    fun getInstitutes(listener: OnFinishedResult, apiLink: String) {
        val institutesQuery = object : JsonArrayRequest(
            Method.GET, "${apiLink}/api/v1/Institute", null,
            Response.Listener { response ->
                listener.onInstitutesSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onInstitutesError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("apiKey" to API_KEY)
        }
        queue.add(institutesQuery)
    }

    fun getTokens(listener: OnFinishedResult, userName: String, password: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "https://$instituteCode.e-kreta.hu/idp/api/v1/Token",
            Response.Listener { response ->
                listener.onTokensSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onTokensError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("apiKey" to API_KEY,
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
            override fun getBody(): ByteArray = "password=$password&institute_code=$instituteCode&grant_type=password&client_id=$CLIENT_ID&userName=$userName".toByteArray()
        }
        queue.add(tokensQuery)
    }
    fun refreshToken(listener: OnFinishedResult, instituteCode: String, refreshToken: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "https://$instituteCode.e-kreta.hu/idp/api/v1/Token",
            Response.Listener { response ->
                listener.onRefreshTokensSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onRefreshTokensError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("apiKey" to API_KEY,
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
            override fun getBody(): ByteArray = "refresh_token=$refreshToken&institute_code=$instituteCode&grant_type=refresh_token&client_id=$CLIENT_ID".toByteArray()
        }
        queue.add(tokensQuery)
    }
    fun getStudent(listener: OnFinishedResult, accessToken: String, refreshToken: String, instituteCode: String) {
        val studentQuery = object : StringRequest(
            Method.GET, "https://$instituteCode.e-kreta.hu/mapi/api/v1/StudentAmi",
            Response.Listener { response ->
                listener.onStudentSuccess(response, accessToken, refreshToken)
            },
            Response.ErrorListener { error ->
                listener.onStudentError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(studentQuery)
    }

    fun getTimetable(listener: OnFinishedResult, accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val timetableQuery = object : StringRequest(
            Method.GET, "https://$instituteCode.e-kreta.hu/mapi/api/v1/LessonAmi?fromDate=${fromDate}&toDate=${toDate}",
            Response.Listener { response ->
                listener.onTimetableSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onTimetableError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(timetableQuery)
    }

    fun getMessageList(listener: OnFinishedResult, accessToken: String) {
        val messageListQuery = object : StringRequest(
            Method.GET, "https://eugyintezes.e-kreta.hu/integration-kretamobile-api/v1/kommunikacio/postaladaelemek/sajat",
            Response.Listener { response ->
                listener.onMessageListSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onMessageListError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(messageListQuery)
    }
    fun getMessage(listener: OnFinishedResult, accessToken: String, messageId: Int) {
        val messageQuery = object : StringRequest(
            Method.GET, "https://eugyintezes.e-kreta.hu/integration-kretamobile-api/v1/kommunikacio/postaladaelemek/$messageId",
            Response.Listener { response ->
                listener.onMessageSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onMessageError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
        }
        queue.add(messageQuery)
    }
    fun setMessageRead(accessToken: String, messageId: Int, isRead: Boolean) {
        val messageReadQuery = object : StringRequest(
            Method.POST,
            "https://eugyintezes.e-kreta.hu/integration-kretamobile-api/v1/kommunikacio/uzenetek/olvasott",
            Response.Listener {},
            Response.ErrorListener {}
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
                "Authorization" to "Bearer ${accessToken}",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent()
            )
            override fun getBodyContentType(): String = "application/json; charset=utf-8"
            override fun getBody(): ByteArray = "{\"isOlvasott\": ${isRead},\"uzenetAzonositoLista\": [${messageId}] }".toByteArray()
        }
        queue.add(messageReadQuery)
    }

    fun getTests(listener: OnFinishedResult, accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val testsQuery = object : StringRequest(
            Method.GET, "https://${instituteCode}.e-kreta.hu/mapi/api/v1/BejelentettSzamonkeresAmi?fromDate=${fromDate}&toDate=${toDate}",
            Response.Listener { response ->
                listener.onTestsSuccess(response)
            },
            Response.ErrorListener { error ->
                listener.onTestsError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
        }
        queue.add(testsQuery)
    }

    private fun makeHomeworkRequest(listener: OnFinishedResult, accessToken: String, instituteCode: String, classHomeworkId: Int, isLast: Boolean): StringRequest {
        val homeworkQuery = object : StringRequest(
            Method.GET, "https://${instituteCode}.e-kreta.hu/mapi/api/v1/HaziFeladat/TanuloHaziFeladatLista/${classHomeworkId}",
            Response.Listener { response ->
                listener.onStudentHomeworkSuccess(response, isLast)
            },
            Response.ErrorListener { error ->
                listener.onStudentHomeworkError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "User-Agent" to getUserAgent())
        }
        return homeworkQuery
    }
    fun getHomework(listener: OnFinishedResult, accessToken: String, instituteCode: String, classHomeworkIds: List<Int>) {
        val homeworkQueries = mutableListOf<StringRequest>()
        for (i in 0 until classHomeworkIds.size) {
            var isLast = classHomeworkIds.size == i + 1
            homeworkQueries.add(makeHomeworkRequest(listener, accessToken, instituteCode, classHomeworkIds[i], isLast))
        }
        for (homeworkQuery in homeworkQueries) {
            queue.add(homeworkQuery)
        }
    }
}