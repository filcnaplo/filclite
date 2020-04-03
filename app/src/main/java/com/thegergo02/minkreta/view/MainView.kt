package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import org.json.JSONArray
import org.json.JSONObject

interface MainView {
    fun setStudent(student: Student)
    fun hideProgress()
    fun showProgress()
    fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>)
    fun displayError(error: String)
}