package com.thegergo02.minkreta.kreta

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URLConnection
import java.util.*

class KretaRequests(ctx: Context) {
    enum class ApiType(val type: String) {
        PROD("PROD"),
        DEV("DEV"),
        UAT("UAT"),
        TEST("TEST")
    }

    interface OnEvaluationListResult {
        fun onEvaluationListSuccess(evals: List<Evaluation>)
        fun onEvaluationListError(error: KretaError)
    }
    interface OnApiLinkResult {
        fun onApiLinkSuccess(link: String)
        fun onApiLinkError(error: KretaError)
    }
    interface OnInstitutesResult {
        fun onInstitutesSuccess(institutes: List<Institute>)
        fun onInstitutesError(error: KretaError)
    }
    interface OnTokensResult {
        fun onTokensSuccess(tokens: Map<String, String>)
        fun onTokensError(error: KretaError)
    }
    interface OnRefreshTokensResult {
        fun onRefreshTokensSuccess(tokens: Map<String, String>)
        fun onRefreshTokensError(error: KretaError)
    }
    interface OnTimetableResult {
        fun onTimetableSuccess(timetable: MutableMap<SchoolDay, MutableList<SchoolClass>>)
        fun onTimetableError(error: KretaError)
    }
    interface OnMessageListResult {
        fun onMessageListSuccess(messageList: List<MessageDescriptor>, sortType: MessageDescriptor.SortType)
        fun onMessageListError(error: KretaError)
    }
    interface OnMessageResult {
        fun onMessageSuccess(messageString: MessageDescriptor)
        fun onMessageError(error: KretaError)
    }
    interface OnTestListResult {
        fun onTestListSuccess(testList: List<Test>)
        fun onTestListError(error: KretaError)
    }
    interface OnHomeworkListResult {
        fun onHomeworkListSuccess(homeworks: List<Homework>)
        fun onHomeworkListError(error: KretaError)
    }
    interface OnStudentDetailsResult {
        fun onStudentDetailsSuccess(student: StudentDetails)
        fun onStudentDetailsError(error: KretaError)
    }

    private val queue = Volley.newRequestQueue(ctx)
    private val API_KEY = "7856d350-1fda-45f5-822d-e1a2f3f1acf0"
    private val CLIENT_ID = "KRETA-ELLENORZO-MOBILE"
    private val LOGIN_API = "https://idp.e-kreta.hu"
    private var userAgent = ""
    private val FALLBACK_USER_AGENT = "hu.ekreta.student/<version>/<codename>"

   init {
       GlobalScope.launch {
           setUserAgent()
       }
   }

    private val USER_AGENT_LINK = "https://www.filcnaplo.hu/PLEASERELEASEV3USERAGENTFILCTHX"
    private fun setUserAgent() {
        val userAgentQuery = JsonObjectRequest(Request.Method.GET, USER_AGENT_LINK, null,
            Response.Listener { response ->
                userAgent = response["KretaUserAgent"].toString().replace("(Android; <codename> 0.0)", "(Android; ${UUID.randomUUID()} ${(5..9)}.${2..9})")
            },
            Response.ErrorListener { error ->
                userAgent = FALLBACK_USER_AGENT.replace("/<version>/<codename>", "/${(5..9)}.${2..9}/${UUID.randomUUID()}")
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

    private val API_HOLDER_LINK = "MAYBE DEPRECATED"
    fun getApiLink(listener: OnApiLinkResult, apiType : ApiType = ApiType.PROD) {
        val apiLinksQuery = JsonObjectRequest(Request.Method.GET, API_HOLDER_LINK, null,
                Response.Listener { response ->
                    val link = response["GlobalMobileApiUrl$apiType"].toString()
                    if (link != null) {
                        listener.onApiLinkSuccess(link)
                    } else {
                        listener.onApiLinkError(KretaError.ParseError("unknown"))
                    }
                },
                Response.ErrorListener { error ->
                    listener.onApiLinkError(KretaError.VolleyError(error.toString(), error))
                }
            )
        queue.add(apiLinksQuery)
    }

    fun getInstitutes(listener: OnInstitutesResult, apiLink: String) {
        val institutesQuery = object : JsonArrayRequest(
            Method.GET, "${apiLink}/api/v3/Institute", null,
            Response.Listener { response ->
                val instituteList = JsonHelper.makeInstitutes(response)
                if (instituteList != null) {
                    listener.onInstitutesSuccess(instituteList)
                } else {
                    listener.onInstitutesError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onInstitutesError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("apiKey" to API_KEY)
        }
        queue.add(institutesQuery)
    }

    fun getTokens(listener: OnTokensResult, userName: String, password: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "$LOGIN_API/connect/token",
            Response.Listener { response ->
                val tokens = JsonHelper.makeTokens(response)
                if (tokens != null) {
                    listener.onTokensSuccess(tokens)
                } else {
                    listener.onTokensError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onTokensError(KretaError.VolleyError(error.toString(), error))
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
    fun refreshToken(listener: OnRefreshTokensResult, refreshToken: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "$LOGIN_API/connect/token",
            Response.Listener { response ->
                val tokens = JsonHelper.makeTokens(response)
                if (tokens != null) {
                    listener.onRefreshTokensSuccess(tokens)
                } else {
                    listener.onRefreshTokensError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onRefreshTokensError(KretaError.VolleyError(error.toString(), error))
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
    fun getEvaluationList(listener: OnEvaluationListResult, accessToken: String, instituteUrl: String) {
        val evalQuery = object : StringRequest(
            Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/Ertekelesek",
            Response.Listener { response ->
                val evals = JsonHelper.makeEvaluationList(response)
                if (evals != null) {
                    listener.onEvaluationListSuccess(evals)
                } else {
                    listener.onEvaluationListError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onEvaluationListError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(evalQuery)
    }

    fun getTimetable(listener: OnTimetableResult, accessToken: String, instituteUrl: String, fromDate: KretaDate, toDate: KretaDate) {
        val timetableQuery = object : StringRequest(
            Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/OrarendElemek?datumTol=${fromDate.toFormattedString(KretaDate.KretaDateFormat.API_DATE)}&datumIg=${toDate.toFormattedString(KretaDate.KretaDateFormat.API_DATE)}",
            Response.Listener { response ->
                val timetable = JsonHelper.makeTimetable(response)
                if (timetable != null) {
                    listener.onTimetableSuccess(timetable)
                } else {
                    listener.onTimetableError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onTimetableError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(timetableQuery)
    }

    fun getMessageList(listener: OnMessageListResult, accessToken: String, sortType: MessageDescriptor.SortType) {
        val messageListQuery = object : StringRequest(
            Method.GET, "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/sajat",
            Response.Listener { response ->
                val messageList = JsonHelper.makeMessageList(response)
                if (messageList != null) {
                    listener.onMessageListSuccess(messageList, sortType)
                } else {
                    listener.onMessageListError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onMessageListError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(messageListQuery)
    }
    fun getMessage(listener: OnMessageResult, accessToken: String, messageId: Int) {
        val messageQuery = object : StringRequest(
            Method.GET, "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/$messageId",
            Response.Listener { response ->
                val message = JsonHelper.makeMessage(response)
                if (message != null) {
                    listener.onMessageSuccess(message)
                } else {
                    listener.onMessageError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onMessageError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
        }
        queue.add(messageQuery)
    }
    fun downloadAttachment(accessToken: String, downloadManager: DownloadManager, attachment: Attachment) {
        val uri = Uri.parse("https://eugyintezes.e-kreta.hu/api/v1/dokumentumok/uzenetek/${attachment.id}")
        val request = DownloadManager.Request(uri)
        val mimeMap = MimeTypeMap.getSingleton()
        request.addRequestHeader("User-Agent", getUserAgent())
            .addRequestHeader("Authorization", "Bearer $accessToken")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI + DownloadManager.Request.NETWORK_MOBILE)
            .setMimeType(mimeMap.getMimeTypeFromExtension(mimeMap.getExtensionFromMimeType(attachment.fileName)) ?: URLConnection.guessContentTypeFromName(attachment.fileName))
            .setTitle(attachment.fileName)
            .setDescription(attachment.id.toString())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/minkreta/${attachment.fileName}")
        downloadManager.enqueue(request)
    }
    fun setMessageRead(accessToken: String, messageId: Int, isRead: Boolean) { //TODO: DOESN'T WORK
        val messageReadQuery = object : StringRequest(
            Method.POST,
            "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/uzenetek/olvasott",
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

    fun getTestList(listener: OnTestListResult, accessToken: String, instituteUrl: String) {
        val testsQuery = object : StringRequest(
            Method.GET, "${instituteUrl}/ellenorzo/V3/Sajat/BejelentettSzamonkeresek",
            Response.Listener { response ->
                val testList = JsonHelper.makeTestList(response)
                if (testList != null) {
                    listener.onTestListSuccess(testList)
                } else {
                    listener.onTestListError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onTestListError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
        }
        queue.add(testsQuery)
    }

    fun getHomeworkList(listener: OnHomeworkListResult, accessToken: String, instituteUrl: String, fromDate: KretaDate) {
        val homeworkQuery = object : StringRequest(
            Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/HaziFeladatok?datumTol=$fromDate",
            Response.Listener { response ->
                val homeworks = JsonHelper.makeHomeworkList(response)
                if (homeworks != null) {
                    listener.onHomeworkListSuccess(homeworks)
                } else {
                    listener.onHomeworkListError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onHomeworkListError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "User-Agent" to getUserAgent())
        }
        queue.add(homeworkQuery)
    }

    fun getStudentDetails(listener: OnStudentDetailsResult, accessToken: String, instituteUrl: String) {
        val studentDetailsQuery = object : StringRequest(
            Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/TanuloAdatlap",
            Response.Listener { response ->
                val studentDetails = JsonHelper.makeStudentDetails(response)
                if (studentDetails != null) {
                    listener.onStudentDetailsSuccess(studentDetails)
                } else {
                    listener.onStudentDetailsError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onStudentDetailsError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "User-Agent" to getUserAgent())
        }
        queue.add(studentDetailsQuery)
    }
}