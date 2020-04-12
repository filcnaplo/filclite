package com.thegergo02.minkreta.kreta

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.Student
import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class KretaRequests(ctx: Context) {
    enum class ApiType(val type: String) {
        PROD("PROD"),
        DEV("DEV"),
        UAT("UAT"),
        TEST("TEST")
    }

    interface OnStudentResult {
        fun onStudentSuccess(student: Student)
        fun onStudentError(error: KretaError)
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
        fun onMessageListSuccess(messageList: List<MessageDescriptor>)
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
    interface OnStudentHomeworkResult {
        fun onStudentHomeworkSuccess(studentHomework: List<StudentHomework?>?)
        fun onStudentHomeworkError(error: KretaError)
    }
    interface OnTeacherHomeworkResult {
        fun onTeacherHomeworkSuccess(teacherHomework: TeacherHomework?)
        fun onTeacherHomeworkError(error: KretaError)
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
            Method.GET, "${apiLink}/api/v1/Institute", null,
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

    fun getTokens(listener: OnTokensResult, userName: String, password: String, instituteUrl: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "$instituteUrl/idp/api/v1/Token",
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
    fun refreshToken(listener: OnRefreshTokensResult, refreshToken: String, instituteUrl: String, instituteCode: String) {
        val tokensQuery = object : StringRequest(
            Method.POST, "$instituteUrl/idp/api/v1/Token",
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
    fun getStudent(listener: OnStudentResult, accessToken: String, instituteUrl: String) {
        val studentQuery = object : StringRequest(
            Method.GET, "$instituteUrl/mapi/api/v1/StudentAmi",
            Response.Listener { response ->
                val student = JsonHelper.makeStudent(response)
                if (student != null) {
                    listener.onStudentSuccess(student)
                } else {
                    listener.onStudentError(KretaError.ParseError("unknown"))
                }
            },
            Response.ErrorListener { error ->
                listener.onStudentError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(studentQuery)
    }

    fun getTimetable(listener: OnTimetableResult, accessToken: String, instituteUrl: String, fromDate: KretaDate, toDate: KretaDate) {
        val timetableQuery = object : StringRequest(
            Method.GET, "$instituteUrl/mapi/api/v1/LessonAmi?fromDate=${fromDate}&toDate=${toDate}",
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
                "Accept" to "application/json",
                "User-Agent" to getUserAgent())
            override fun getBodyContentType(): String = "application/x-www-form-urlencoded"
        }
        queue.add(timetableQuery)
    }

    fun getMessageList(listener: OnMessageListResult, accessToken: String) {
        val messageListQuery = object : StringRequest(
            Method.GET, "https://eugyintezes.e-kreta.hu/integration-kretamobile-api/v1/kommunikacio/postaladaelemek/sajat",
            Response.Listener { response ->
                val messageList = JsonHelper.makeMessageList(response)
                if (messageList != null) {
                    listener.onMessageListSuccess(messageList)
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
            Method.GET, "https://eugyintezes.e-kreta.hu/integration-kretamobile-api/v1/kommunikacio/postaladaelemek/$messageId",
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

    fun getTestList(listener: OnTestListResult, accessToken: String, instituteUrl: String, fromDate: KretaDate, toDate: KretaDate) {
        val testsQuery = object : StringRequest(
            Method.GET, "${instituteUrl}/mapi/api/v1/BejelentettSzamonkeresAmi?fromDate=${fromDate}&toDate=${toDate}",
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

    private fun makeStudentHomeworkRequest(homeworkCollector: HomeworkCollector, accessToken: String, instituteUrl: String, classHomeworkId: Int): StringRequest {
        val homeworkQuery = object : StringRequest(
            Method.GET, "$instituteUrl/mapi/api/v1/HaziFeladat/TanuloHaziFeladatLista/$classHomeworkId",
            Response.Listener { response ->
                val studentHomework = JsonHelper.makeStudentHomework(response)
                homeworkCollector.getStudentHomeworkListener()
                    .onStudentHomeworkSuccess(studentHomework)
            },
            Response.ErrorListener { error ->
                homeworkCollector.getStudentHomeworkListener().onStudentHomeworkError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json; charset: utf-8",
                "User-Agent" to getUserAgent())
        }
        return homeworkQuery
    }
    private fun makeTeacherHomeworkRequest(homeworkCollector: HomeworkCollector, accessToken: String, instituteUrl: String, classHomeworkId: Int): StringRequest {
        val homeworkQuery = object : StringRequest(
            Method.GET, "$instituteUrl/mapi/api/v1/HaziFeladat/TanarHaziFeladat/$classHomeworkId",
            Response.Listener { response ->
                val teacherHomework = JsonHelper.makeTeacherHomework(response)
                homeworkCollector.getTeacherHomeworkListener()
                    .onTeacherHomeworkSuccess(teacherHomework)
            },
            Response.ErrorListener { error ->
                homeworkCollector.getTeacherHomeworkListener().onTeacherHomeworkError(KretaError.VolleyError(error.toString(), error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf("Authorization" to "Bearer $accessToken",
                "Accept" to "application/json; charset: utf-8",
                "User-Agent" to getUserAgent())
        }
        return homeworkQuery
    }
    fun getHomework(homeworkCollector: HomeworkCollector, accessToken: String, instituteUrl: String, classHomeworkIds: List<Int>) {
        val homeworkQueries = mutableListOf<StringRequest>()
        for (id in classHomeworkIds) {
            homeworkQueries.add(makeStudentHomeworkRequest(homeworkCollector, accessToken, instituteUrl, id))
            homeworkQueries.add(makeTeacherHomeworkRequest(homeworkCollector, accessToken, instituteUrl, id))
        }
        for (homeworkQuery in homeworkQueries) {
            queue.add(homeworkQuery)
        }
    }
}