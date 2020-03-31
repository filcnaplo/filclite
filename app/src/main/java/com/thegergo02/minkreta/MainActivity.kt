package com.thegergo02.minkreta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.ui.AbsencesUI
import com.thegergo02.minkreta.ui.EvaluationUI
import com.thegergo02.minkreta.ui.HomeworksUI
import com.thegergo02.minkreta.ui.NotesUI
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var cachedStudent: Student
    private lateinit var itemHolders: Map<Tab, LinearLayout>

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

        itemHolders = mutableMapOf<Tab, LinearLayout>(
            Tab.Evaluations to eval_holder_ll,
            Tab.Notes to note_holder_ll,
            Tab.Absences to abs_holder_ll,
            Tab.Homeworks to homework_holder_ll
        )

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
            switchTab(Tab.Evaluations)
        }
        notes_btt.setOnClickListener {
            switchTab(Tab.Notes)
        }
        abs_btt.setOnClickListener {
            switchTab(Tab.Absences)
        }
        homeworks_btt.setOnClickListener {
            switchTab(Tab.Homeworks)
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

    enum class Tab {
        Evaluations,
        Notes,
        Absences,
        Homeworks,
        Messages
    }

    private fun closeTabs(exception: Tab? = null) {
        for (tabHolder in itemHolders) {
            if (tabHolder.key != exception) {
                tabHolder.value.visibility = View.GONE
            }
        }
    }
    private fun switchTab(newTab: Tab) {
        closeTabs(newTab)
        val tabHolder = itemHolders.get(newTab)
        if (tabHolder?.visibility == View.GONE) {
            tabHolder?.visibility = View.VISIBLE
        } else {
            tabHolder?.visibility = View.GONE
        }
    }

    private fun refreshUI() {
        showProgress()
        closeTabs()
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        EvaluationUI.generateEvaluations(this, cachedStudent, itemHolders.get(Tab.Evaluations), details_ll, ::showDetails, ::hideDetails)
        NotesUI.generateNotes(this, cachedStudent, itemHolders.get(Tab.Notes), details_ll, ::showDetails, ::hideDetails)
        AbsencesUI.generateAbsences(this, cachedStudent, itemHolders.get(Tab.Absences), details_ll, ::showDetails, ::hideDetails)
        HomeworksUI.generateHomeworks(this, cachedStudent, itemHolders.get(Tab.Homeworks), details_ll, ::showDetails, ::hideDetails)
        hideProgress()
    }
}
