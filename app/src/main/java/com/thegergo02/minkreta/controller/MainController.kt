package com.thegergo02.minkreta.controller

import com.android.volley.AuthFailureError
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.KretaDateAdapter
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.homework.StudentHomework
import com.thegergo02.minkreta.data.homework.TeacherHomework
import com.thegergo02.minkreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.data.timetable.SchoolDayOrder
import com.thegergo02.minkreta.data.timetable.Test
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainController(private var mainView: MainView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    private lateinit var currentStudent: Student
    fun getStudent(accessToken: String, refreshToken: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudent(parentListener, accessToken, refreshToken, instituteCode)
        }
    }

    fun getTimetable(accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, accessToken, instituteCode, fromDate, toDate)
        }
    }

    fun getMessageList(accessToken: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessageList(parentListener, accessToken)
        }
    }
    fun getMessage(accessToken: String, messageId: Int) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessage(parentListener, accessToken, messageId)
        }
    }
    fun setMessageRead(accessToken: String, messageId: Int, isRead: Boolean = true) {
        GlobalScope.launch {
            apiHandler.setMessageRead(accessToken, messageId, isRead)
        }
    }

    fun refreshToken(instituteCode: String, refreshToken: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.refreshToken(parentListener, instituteCode, refreshToken)
        }
    }

    fun getTests(accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTests(parentListener, accessToken, instituteCode, fromDate, toDate)
        }
    }

    fun getHomework(accessToken: String, instituteCode: String, homeworkIds: List<Int>) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getHomework(parentListener, accessToken, instituteCode, homeworkIds)
        }
    }

    fun makeTimetable(timetable: String): MutableMap<SchoolDay, MutableList<SchoolClass>> {
        val returnedTimetable = mutableMapOf<SchoolDay, MutableList<SchoolClass>>()
        val timetableJson = JSONArray(timetable)
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<SchoolClass> = moshi.adapter(SchoolClass::class.java)
        for (day in SchoolDayOrder.schoolDayOrder)
        {
            returnedTimetable[day] = mutableListOf()
        }
        for (i in 0 until timetableJson.length()) {
            val schoolClass = adapter.fromJson(timetableJson[i].toString())
            if (schoolClass != null) {
                val schoolDay = schoolClass.startTime.toSchoolDay()
                returnedTimetable[schoolDay]?.add(schoolClass)
            }
        }
        for (day in SchoolDayOrder.schoolDayOrder) {
            val schoolDay = returnedTimetable[day]
            if (schoolDay != null)
                if (schoolDay.isEmpty()) {
                    returnedTimetable.remove(day)
                }
        }
        return returnedTimetable
    }

    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<Student> = moshi.adapter(Student::class.java)
        val newStudent = adapter.fromJson(student)
        if (newStudent != null) currentStudent = newStudent
        currentStudent.accessToken = accessToken
        currentStudent.refreshToken = refreshToken
        mainView?.setStudent(currentStudent)
    }
    override fun onStudentError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
    /*    val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }*/
        val errorString = error.toString()
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onTimetableSuccess(timetableString: String) {
        mainView?.generateTimetable(makeTimetable(timetableString))
    }
    override fun onTimetableError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
        /*val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }*/
        val errorString = error.toString()
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onMessageListSuccess(messageListString: String) {
        if (messageListString.isEmpty()) {
            onMessageError(ServerError())
            return
        }
        val messageListJson = JSONArray(messageListString)
        val messageList = mutableListOf<MessageDescriptor>()
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<MessageDescriptor> = moshi.adapter(MessageDescriptor::class.java)
        for (i in 0 until messageListJson.length()) {
            val messageDescriptor = adapter.fromJson(messageListJson[i].toString())
            if (messageDescriptor != null) {
                messageList.add(messageDescriptor)
            }
        }
        mainView?.generateMessageDescriptors(messageList.reversed())
    }
    override fun onMessageListError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
        /*val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            is ServerError -> Strings.get(R.string.server_error_general)
            else -> error.toString()
        }*/
        val errorString = error.toString()
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onMessageSuccess(messageString: String) {
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<MessageDescriptor> = moshi.adapter(MessageDescriptor::class.java)
        val message = adapter.fromJson(messageString)
        if (message != null) {
            mainView?.generateMessage(message)
        }
    }
    override fun onMessageError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
/*        val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            is ServerError -> Strings.get(R.string.server_error_general)
            else -> error.toString()
        }*/
        val errorString = error.toString()
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onRefreshTokensSuccess(tokens: String) {
        val tokensJson = JSONObject(tokens)
        mainView?.refreshToken(tokensJson)
        mainView?.hideProgress()
    }
    override fun onRefreshTokensError(error: VolleyError) {
        mainView?.sendToLogin()
    }

    override fun onTestsSuccess(testsString: String) {
        val returnedTests = mutableListOf<Test>()
        val testsJson = JSONArray(testsString)
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<Test> = moshi.adapter(Test::class.java)
        for (i in 0 until testsJson.length()) {
            val test = adapter.fromJson(testsJson[i].toString())
            if (test != null) {
                returnedTests.add(test)
            }
        }
        mainView?.generateTests(returnedTests)
    }
    override fun onTestsError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
    }

    override fun onStudentHomeworkSuccess(homeworkListString: String) {
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<StudentHomework> = moshi.adapter(StudentHomework::class.java)
        val homeworkJSONArray = JSONArray(homeworkListString)
        if (homeworkJSONArray.length() == 0) {
            mainView?.collectStudentHomework(null)
            return
        }
        val homeworkList = mutableListOf<StudentHomework?>()
        for (i in 0 until homeworkJSONArray.length()) {
            val homeworkString = homeworkJSONArray[i].toString()
            homeworkList.add(adapter.fromJson(homeworkString))
        }
        mainView?.collectStudentHomework(homeworkList)
    }
    override fun onStudentHomeworkError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
    }

    override fun onTeacherHomeworkSuccess(homeworkString: String) {
        val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
        val adapter: JsonAdapter<TeacherHomework> = moshi.adapter(TeacherHomework::class.java)
        val homework = adapter.fromJson(homeworkString)
        mainView?.collectTeacherHomework(homework)
    }
    override fun onTeacherHomeworkError(error: VolleyError) {
        when (error) {
            is AuthFailureError -> {mainView?.triggerRefreshToken()}
        }
    }

    override fun onTokensSuccess(tokens: String) {}
    override fun onTokensError(error: VolleyError) {}
    override fun onApiLinkSuccess(link: String) {}
    override fun onApiLinkError(error: String) {}
    override fun onInstitutesSuccess(institutes: JSONArray) {}
    override fun onInstitutesError(error: VolleyError) {}
}
