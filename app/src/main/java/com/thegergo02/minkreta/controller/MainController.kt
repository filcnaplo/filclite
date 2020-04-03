package com.thegergo02.minkreta.controller

import android.util.Log
import com.android.volley.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.view.MainView
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.data.timetable.SchoolDayOrder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.time.days

class MainController(private var mainView: MainView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    private lateinit var currentStudent: Student
    val apiLinkHelper = ApiLinkHelper(apiHandler)

    fun getStudent(accessToken: String, refreshToken: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudent(parentListener, accessToken, refreshToken, instituteCode)
        }
    }

    fun getTimetable(accessToken: String, refreshToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, accessToken, refreshToken, instituteCode, fromDate.toString(), toDate.toString())
        }
    }

    fun onDestroy() {
        mainView = null
    }

    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Student> = moshi.adapter(Student::class.java)
        var newStudent = adapter.fromJson(student)
        if (newStudent != null) currentStudent = newStudent
        currentStudent.accessToken = accessToken
        currentStudent.refreshToken = refreshToken
        mainView?.setStudent(currentStudent)
    }
    override fun onStudentError(error: VolleyError) {
        var errorString: String
        when(error) {
            is AuthFailureError -> errorString = "Wrong credetinals! (AuthFailureError)"
            is TimeoutError -> errorString = "The KRETA server took too long to respond. (TimeoutError)"
            is NetworkError -> errorString = "Maybe the request got interrupted? (NetworkError) (${error.message})"
            is NoConnectionError -> errorString = "Can't get student data without an internet connection."
            else -> errorString = error.toString()
        }
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onTokensSuccess(tokens: String) {}
    override fun onTokensError(error: VolleyError) {}
    override fun onApiLinkSuccess(link: String) {}
    override fun onApiLinkError(error: String) {}
    override fun onInstitutesSuccess(institutes: JSONArray) {}
    override fun onInstitutesError(error: VolleyError) {}
    override fun onTimetableSuccess(timetable: String) {
        var returnedTimetable = mutableMapOf<SchoolDay, MutableList<SchoolClass>>()
        var timetableJson = JSONArray(timetable)
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<SchoolClass> = moshi.adapter(SchoolClass::class.java)
        val calendar = Calendar.getInstance()

        for (day in SchoolDayOrder.schoolDayOrder)
        {
            returnedTimetable[day] = mutableListOf()
        }
        for (i in 0 until timetableJson.length()) {
            val schoolClass = adapter.fromJson(timetableJson[i].toString())
            val kretaDate = KretaDate().fromString(schoolClass?.startTime)
            val schoolDay = SchoolDayOrder.schoolDayOrder[kretaDate.toLocalDateTime().dayOfWeek.value - 1]
            Log.w("day", schoolDay.toString())
            Log.w("day", kretaDate.toString())
            if (schoolDay != null && schoolClass != null) {
                returnedTimetable[schoolDay]?.add(schoolClass)
            }
        }
        mainView?.generateTimetable(returnedTimetable)
    }
    override fun onTimetableError(error: VolleyError) {
        var errorString: String
        when(error) {
            is AuthFailureError -> errorString = "Wrong credetinals! (AuthFailureError)"
            is TimeoutError -> errorString = "The KRETA server took too long to respond. (TimeoutError)"
            is NetworkError -> errorString = "Maybe the request got interrupted? (NetworkError) (${error.message})"
            is NoConnectionError -> errorString = "Can't get timetable without an internet connection."
            else -> errorString = error.toString()
        }
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }
}
