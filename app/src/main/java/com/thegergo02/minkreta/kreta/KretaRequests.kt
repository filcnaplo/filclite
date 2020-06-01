package com.thegergo02.minkreta.kreta

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.homework.HomeworkComment
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.message.Receiver
import com.thegergo02.minkreta.kreta.data.message.Worker
import com.thegergo02.minkreta.kreta.data.sub.*
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.net.URLConnection
import java.util.*


class KretaRequests(ctx: Context) {
    private var accessToken = ""
    private var refreshToken = ""
    private var instituteCode = ""
    private var instituteUrl = ""
    private lateinit var tokenListener: OnRefreshTokensResult

    constructor(ctx: Context, listener: OnRefreshTokensResult, accessToken: String, refreshToken: String, instituteCode: String) : this(ctx) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.instituteCode = instituteCode
        this.instituteUrl = "https://$instituteCode.e-kreta.hu"
        tokenListener = listener
    }

    interface OnEvaluationListResult {
        fun onEvaluationListSuccess(evals: List<Evaluation>)
        fun onEvaluationListError(error: KretaError)
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
    interface OnNoteListResult {
        fun onNoteListSuccess(testList: List<Note>)
        fun onNoteListError(error: KretaError)
    }
    interface OnNoticeListResult {
        fun onNoticeListSuccess(testList: List<Notice>)
        fun onNoticeListError(error: KretaError)
    }
    interface OnHomeworkListResult {
        fun onHomeworkListSuccess(homeworks: List<Homework>)
        fun onHomeworkListError(error: KretaError)
    }
    interface OnHomeworkCommentListResult {
        fun onHomeworkCommentListSuccess(homeworkComments: List<HomeworkComment>)
        fun onHomeworkCommentListError(error: KretaError)
    }
    interface OnAbsenceListResult {
        fun onAbsenceListSuccess(homeworks: List<Absence>)
        fun onAbsenceListError(error: KretaError)
    }
    interface OnStudentDetailsResult {
        fun onStudentDetailsSuccess(student: StudentDetails)
        fun onStudentDetailsError(error: KretaError)
    }
    interface OnSendHomeworkCommentResult {
        fun onSendHomeworkSuccess(homeworkUid: String)
        fun onSendHomeworkError(error: KretaError)
    }
    interface OnSendMessageResult {
        fun onSendMessageSuccess()
        fun onSendMessageError(error: KretaError)
    }
    interface OnWorkersResult {
        fun onWorkersSuccess(workers: List<Worker>)
        fun onWorkersError(error: KretaError)
    }
    interface OnSendableReceiverTypesResult {
        fun onSendableReceiverTypesSuccess(types: List<Type>)
        fun onSendableReceiverTypesError(error: KretaError)
    }
    interface OnUploadTemporaryAttachmentResult {
        fun onUploadTemporaryAttachmentSuccess(attachment: Attachment)
        fun onUploadTemporaryAttachmentError(error: KretaError)
    }

    private var userAgent = "hu.ekreta.student/<version>/<codename>"
    private var apiKey = "7856d350-1fda-45f5-822d-e1a2f3f1acf0"
    private var clientId = "KRETA-ELLENORZO-MOBILE"
    private var loginUrl = "https://idp.e-kreta.hu"
    private var apiUrl = "https://kretaglobalmobileapi2.ekreta.hu"

    private val networkHelper = NetworkHelper(ctx)

    init {
        GlobalScope.launch {
            getKretaDetails()
        }
    }

    private fun getKretaDetails() {
        val successListener = Response.Listener<JSONObject> { response ->
            userAgent = response["kretaUserAgent"].toString().replace("/<version>/<codename>", "/${(5..9)}.${2..9}/${UUID.randomUUID()}")
            apiKey = response["apiKey"].toString()
            clientId = response["clientId"].toString()
            loginUrl = response["loginUrl"].toString()
        }
        val errorListener = Response.ErrorListener {
            userAgent = userAgent.replace("/<version>/<codename>", "/${(5..9)}.${2..9}/${UUID.randomUUID()}")
        }
        val request = NetworkJsonObjectRequest(Request.Method.GET, "https://thegergo02.github.io/settings.json", successListener, errorListener)
        networkHelper.requestJsonObject(request)
    }
    fun getUserAgent() : String {
        while (userAgent == "") {
            continue
        }
        return userAgent
    }

    private fun isRefreshTokenNeeded(error: VolleyError): Boolean {
        return when(error) {
            is AuthFailureError -> true
            else -> false
        }
    }

    fun getInstitutes(listener: OnInstitutesResult) {
        val successListener = Response.Listener<JSONArray> { response ->
            val institutes = JsonHelper.makeInstitutes(response)
            if (institutes != null) {
                listener.onInstitutesSuccess(institutes)
            } else {
                listener.onInstitutesError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onInstitutesError(KretaError.VolleyError(error.toString(), error))
        }
        val request = NetworkJsonArrayRequest(Request.Method.GET,
            "${apiUrl}/api/v3/Institute",
            successListener,
            errorListener,
            mapOf(NetworkHelper.Header.ApiKey to apiKey))
        networkHelper.requestJsonArray(request)
    }

    fun getTokens(listener: OnTokensResult, userName: String, password: String, instituteCode: String) {
        val successListener = Response.Listener<String> { response ->
            val tokens = JsonHelper.makeTokens(response)
            if (tokens != null) {
                listener.onTokensSuccess(tokens)
            } else {
                listener.onTokensError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onTokensError(KretaError.VolleyError(error.toString(), error))
        }
        val headers = mapOf(
            NetworkHelper.Header.ApiKey to apiKey,
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.POST,
            "$loginUrl/connect/token",
            successListener,
            errorListener,
            headers,
            "application/x-www-form-urlencoded",
            "password=$password&institute_code=$instituteCode&grant_type=password&client_id=$clientId&userName=$userName"
        )
        networkHelper.requestString(request)
    }
    fun refreshToken(listener: OnRefreshTokensResult) {
        val successListener =  Response.Listener<String> { response ->
            val tokens = JsonHelper.makeTokens(response)
            if (tokens != null) {
                accessToken = tokens["access_token"].toString()
                refreshToken = tokens["refresh_token"].toString()
                listener.onRefreshTokensSuccess(tokens)
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onRefreshTokensError(KretaError.VolleyError(error.toString(), error))
        }
        val headers = mapOf(
            NetworkHelper.Header.ApiKey to apiKey,
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.POST,
            "$loginUrl/connect/token",
        successListener,
        errorListener,
        headers,
        "application/x-www-form-urlencoded",
        "refresh_token=$refreshToken&institute_code=$instituteCode&grant_type=refresh_token&client_id=$clientId")
        networkHelper.requestString(request)
    }
    fun getEvaluationList(listener: OnEvaluationListResult) {
        val successListener = Response.Listener<String> {  response ->
            val evals = JsonHelper.makeEvaluationList(response)
            if (evals != null) {
                listener.onEvaluationListSuccess(evals)
            } else {
                listener.onEvaluationListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onEvaluationListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET,
            "$instituteUrl/ellenorzo/V3/Sajat/Ertekelesek",
        successListener,
        errorListener,
        headers)
        networkHelper.requestString(request)
    }
    fun getNoteList(listener: OnNoteListResult) {
        val successListener = Response.Listener<String> { response ->
            val notes = JsonHelper.makeNoteList(response)
            if (notes != null) {
                listener.onNoteListSuccess(notes)
            } else {
                listener.onNoteListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onNoteListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/Feljegyzesek", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun getNoticeList(listener: OnNoticeListResult) {
        val successListener = Response.Listener<String> { response ->
            val notice = JsonHelper.makeNoticeList(response)
            if (notice != null) {
                listener.onNoticeListSuccess(notice)
            } else {
                listener.onNoticeListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onNoticeListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/FaliujsagElemek", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun getAbsenceList(listener: OnAbsenceListResult) {
        val successListener = Response.Listener<String> { response ->
            val absences = JsonHelper.makeAbsenceList(response)
            if (absences != null) {
                listener.onAbsenceListSuccess(absences)
            } else {
                listener.onAbsenceListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onAbsenceListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/Mulasztasok", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }

    fun getTimetable(listener: OnTimetableResult, fromDate: KretaDate, toDate: KretaDate) {
        val successListener = Response.Listener<String> { response ->
            val timetable = JsonHelper.makeTimetable(response)
            if (timetable != null) {
                listener.onTimetableSuccess(timetable)
            } else {
                listener.onTimetableError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onTimetableError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/OrarendElemek?datumTol=${fromDate.toFormattedString(KretaDate.KretaDateFormat.API_DATE)}&datumIg=${toDate.toFormattedString(KretaDate.KretaDateFormat.API_DATE)}", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }

    fun getMessageList(listener: OnMessageListResult, type: MessageDescriptor.Type) {
        val successListener = Response.Listener<String> { response ->
            val messageList = JsonHelper.makeMessageList(response)
            if (messageList != null) {
                listener.onMessageListSuccess(messageList)
            } else {
                listener.onMessageListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onMessageListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, type.endpoint, successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun sendMessage(listener: OnSendMessageResult, receivers: List<Receiver>, attachments: List<Attachment>, subject: String, content: String, replyId: Int? = null) {
        var receiversText = ""
        var attachmentText = ""
        val replyText = if (replyId != null) { ",\"elozoUzenetAzonosito\":$replyId" } else { "" }
        for (receiver in receivers) {
            receiversText += "$receiver,"
        }
        receiversText = receiversText.dropLast(1)
        for (attachment in attachments) {
            attachmentText += "$attachment,"
        }
        attachmentText = attachmentText.dropLast(1)
        val body = "{\"cimzettLista\":[$receiversText],\"csatolmanyok\":[$attachmentText], \"targy\":\"$subject\", \"szoveg\":\"$content\" $replyText}"
        val successListener = Response.Listener<String> { listener.onSendMessageSuccess() }
        val errorListener = Response.ErrorListener {  error ->
            listener.onSendMessageError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.ManagementLocalization to "hu-HU",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.POST,
            "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/uzenetek",
            successListener,
            errorListener,
            headers,
            "application/json; charset=utf-8",
            body)
        networkHelper.requestString(request)
    }
    fun getMessage(listener: OnMessageResult, messageId: Int) {
        val successListener = Response.Listener<String> { response ->
            val message = JsonHelper.makeMessage(response)
            if (message != null) {
                listener.onMessageSuccess(message)
            } else {
                listener.onMessageError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onMessageError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/$messageId", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun downloadAttachment(downloadManager: DownloadManager, attachment: Attachment) {
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
    fun uploadTemporaryAttachment(listener: OnUploadTemporaryAttachmentResult, attachment: Attachment) {
        val successListener = Response.Listener<NetworkResponse> {
            attachment.temporaryId = JsonHelper.temporaryIdFromString(it.data.toString(Charsets.UTF_8))
            listener.onUploadTemporaryAttachmentSuccess(attachment)
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onUploadTemporaryAttachmentError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val mimeMap = MimeTypeMap.getSingleton()
        val mimeType = mimeMap.getMimeTypeFromExtension(mimeMap.getExtensionFromMimeType(attachment.fileName)) ?: URLConnection.guessContentTypeFromName(attachment.fileName)
        var part = MultiPart()
        attachment.inputStream?.use {
            val data: ByteArray = it.readBytes()
            part = MultiPart(null, MultipartRequest.FilePart("fajl", mimeType, attachment.fileName, data))

        }
        attachment.inputStream = null
        val request = NetworkMultiPartRequest(Request.Method.GET,
            "https://eugyintezes.e-kreta.hu/api/v1/ideiglenesfajlok",
            successListener,
            errorListener,
            listOf(part),
            headers)
        networkHelper.requestMultiPart(request)
    }
    fun setMessageRead(messageId: Int, isRead: Boolean) {
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.POST,
            "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/olvasott",
            Response.Listener {},
            Response.ErrorListener {},
            headers,
            "application/json; charset=utf-8",
            "{\"isOlvasott\": ${isRead},\"postaladaElemAzonositoLista\": [${messageId}] }")
        networkHelper.requestString(request)
    }

    fun getTestList(listener: OnTestListResult) {
        val successListener = Response.Listener<String> { response ->
            val tests = JsonHelper.makeTestList(response)
            if (tests != null) {
                listener.onTestListSuccess(tests)
            } else {
                listener.onTestListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onTestListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/BejelentettSzamonkeresek", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }

    fun getHomeworkList(listener: OnHomeworkListResult, fromDate: KretaDate) {
        val successListener = Response.Listener<String> { response ->
            val homeworks = JsonHelper.makeHomeworkList(response)
            if (homeworks != null) {
                listener.onHomeworkListSuccess(homeworks)
            } else {
                listener.onHomeworkListError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onHomeworkListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/HaziFeladatok?datumTol=$fromDate", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun getHomeworkCommentList(listener: OnHomeworkCommentListResult, homeworkUid: String) {
        val successListener = Response.Listener<String> { response ->
            val comments = JsonHelper.makeHomeworkCommentList(response)
            if (comments != null) {
                listener.onHomeworkCommentListSuccess(comments)
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onHomeworkCommentListError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET, "$instituteUrl/ellenorzo/V3/Sajat/HaziFeladatok/$homeworkUid/Kommentek", successListener, errorListener, headers)
        networkHelper.requestString(request)
    }
    fun sendHomeworkComment(listener: OnSendHomeworkCommentResult, homeworkUid: String, text: String) {
        val successListener = Response.Listener<String> { response ->
            listener.onSendHomeworkSuccess(homeworkUid)
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onSendHomeworkError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.POST,
            "$instituteUrl/ellenorzo/V3/Sajat/Orak/TanitasiOrak/HaziFeladatok/Kommentek",
            successListener,
            errorListener,
            headers,
        "application/json; charset=utf-8",
        "{\"HaziFeladatUid\":$homeworkUid,\"FeladatSzovege\":\"$text\"}")
        networkHelper.requestString(request)
    }

    fun getStudentDetails(listener: OnStudentDetailsResult) {
        val successListener = Response.Listener<String> { response ->
            val studentDetails = JsonHelper.makeStudentDetails(response)
            if (studentDetails != null) {
                listener.onStudentDetailsSuccess(studentDetails)
            } else {
                listener.onStudentDetailsError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onStudentDetailsError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET,
            "$instituteUrl/ellenorzo/V3/Sajat/TanuloAdatlap",
            successListener,
            errorListener,
            headers)
        networkHelper.requestString(request)
    }
    enum class ReceiverType(val endpoint: String?, val type: Type) {
        Guardian(null,
            Type(1, "GONDVISELO", "Gondviselő", "Gondviselő", "Gondviselő")),
        Student(null,
            Type(2, "TANULO", "Tanuló", "Tanuló", "Tanuló")),
        ClassTutelary("kreta/gondviselok/osztaly",
            Type(3, "OSZTALY_SZULO", "Osztály - Szülő", "Osztály - Szülő", "Osztály - Szülő")),
        ClassStudent("kreta/tanulok/osztalyok",
            Type(4, "OSZTALY_TANULO", "Osztály - Tanuló", "Osztály - Tanuló", "Osztály - Tanuló")),
        ClassGroupTutelary( "kreta/gondviselok/tanoraicsoport",
            Type(5, "TANORAICSOPORT_SZULO", "Tanórai csoport - Szülő", "Tanórai csoport - Szülő", "Tanórai csoport - Szülő")),
        ClassGroupStudent( "kreta/tanulok/tanoraicsoportok",
            Type(6, "TANORAICSOPORT_TANULO", "Tanórai csoport - Tanuló", "Tanórai csoport - Tanuló", "Tanórai csoport - Tanuló")),
        Principal("kreta/alkalmazottak/igazgatosag",
            Type(7, "IGAZGATOSAG", "Igazgatóság", "Igazgatóság", "Igazgatóság")),
        ClassroomTeacher("kreta/alkalmazottak/oszalyfonok",
            Type(8, "OSZTALYFONOK", "Osztályfőnök", "Osztályfőnök", "Osztályfőnök")),
        Teacher("kreta/alkalmazottak/tanar",
            Type(9, "TANAR", "Tanár", "Tanár", "Tanár")),
        Admin("kreta/alkalmazottak/adminisztrator",
            Type(10, "ADMIN", "Adminisztrátor", "Adminisztrátor", "Adminisztrátor")),
        SzmkRepresentative("kommunikacio/szmkkepviselok/cimezheto",
            Type(11, "SZMK_KEPVISELO", "SZMK képviselő", "SZMK képviselő", "SZMK képviselő"))
    }
    fun getReceivers(listener: OnWorkersResult, type: ReceiverType) {
        val successListener = Response.Listener<String> { response ->
            val workers = JsonHelper.makeWorkers(response, type.type)
            if (workers != null) {
                listener.onWorkersSuccess(workers)
            } else {
                listener.onWorkersError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onWorkersError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET,
            "https://eugyintezes.e-kreta.hu/api/v1/${type.endpoint}",
            successListener,
            errorListener,
            headers)
        networkHelper.requestString(request)
    }
    fun getSendableReceiverTypes(listener: OnSendableReceiverTypesResult) {
        val successListener = Response.Listener<String> { response ->
            val workers = JsonHelper.makeTypes(response)
            if (workers != null) {
                listener.onSendableReceiverTypesSuccess(workers)
            } else {
                listener.onSendableReceiverTypesError(KretaError.ParseError("unknown"))
            }
        }
        val errorListener = Response.ErrorListener { error ->
            listener.onSendableReceiverTypesError(KretaError.VolleyError(error.toString(), error))
            if (isRefreshTokenNeeded(error)) {
                refreshToken(tokenListener)
            }
        }
        val headers = mapOf(
            NetworkHelper.Header.Auth to "Bearer $accessToken",
            NetworkHelper.Header.Accept to "application/json",
            NetworkHelper.Header.UserAgent to getUserAgent()
        )
        val request = NetworkStringRequest(Request.Method.GET,
            "https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/cimezhetotipusok",
            successListener,
            errorListener,
            headers)
        networkHelper.requestString(request)
    }
}