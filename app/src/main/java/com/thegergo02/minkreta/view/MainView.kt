package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.data.Student
import org.json.JSONArray
import org.json.JSONObject

interface MainView {
    fun setStudent(student: Student)
    fun hideProgress()
    fun showProgress()
    fun displayError(error: String)
}