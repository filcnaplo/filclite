package com.thegergo02.minkreta

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.transition.Visibility
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.ui.EvaluationUI
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var cachedStudent: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = MainController(this, ApiHandler(this))

        val accessToken = intent.getStringExtra("access_token")
        val refreshToken = intent.getStringExtra("refresh_token")
        val instituteCode = intent.getStringExtra("institute_code")

        if (accessToken != null && refreshToken != null && instituteCode != null) {
            controller.getStudent(accessToken, refreshToken, instituteCode)
            showProgress()
        }

        name_tt.setOnClickListener {
            if (details_ll.visibility == View.GONE) {
                hideDetails()
                val nameDetailsTextView = TextView(this)
                nameDetailsTextView.text = "ID: ${cachedStudent.id} \n" +
                        "SchoolYearID: ${cachedStudent.schoolYearId} \n" +
                        "Name: ${cachedStudent.name} \n" +
                        "NameOfBirth: ${cachedStudent.nameOfBirth} \n" +
                        "PlaceOfBirth: ${cachedStudent.placeOfBirth} \n" +
                        "Mother's name: ${cachedStudent.mothersName} \n" +
                        "AddressDataList: ${cachedStudent.addressDataList} \n" +
                        "DateOfBirtUTC: ${cachedStudent.DateOfBirthUtc} \n" +
                        "InstituteName: ${cachedStudent.instituteName} \n" +
                        "InstituteCode: ${cachedStudent.instituteCode} \n" +
                        "Lessons: ${cachedStudent.lessons} \n" +
                        "Events: ${cachedStudent.events}"
                nameDetailsTextView.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                details_ll.addView(nameDetailsTextView)
                showDetails()
            } else {
                hideDetails()
            }
        }

        evals_btt.setOnClickListener {
            if (eval_holder_ll.visibility == View.GONE) {
                eval_holder_ll.visibility = View.VISIBLE
            } else {
                eval_holder_ll.visibility = View.GONE
            }
        }
    }
    
    override fun hideProgress() {
        loading_bar.visibility = View.GONE
        name_tt.visibility = View.VISIBLE
    }
    override fun showProgress() {
        loading_bar.visibility = View.VISIBLE
        name_tt.visibility = View.GONE
    }

    override fun setStudent(student: Student) {
        cachedStudent = student
        refreshUI()
    }

    override fun displayError(error: String) {
        val errorSnack = Snackbar.make(main_cl, error, Snackbar.LENGTH_LONG)
        errorSnack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError))
        errorSnack.show()
    }

    private fun showDetails() {
        details_ll.visibility = View.VISIBLE
        scroll_view.smoothScrollTo(0, 0)
    }
    private fun hideDetails() {
        details_ll.visibility = View.GONE
        details_ll.removeAllViews()
    }

    private fun refreshUI() {
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        EvaluationUI.generateEvaluations(this, cachedStudent, eval_holder_ll, details_ll, ::showDetails, ::hideDetails)
        hideProgress()
    }
}
