package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework

class HomeworkUI {
    companion object {
        fun generateStudentHomework(ctx: Context, homeworkList: List<StudentHomework>, homeworkHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (homework in homeworkList) {
                val text = "${homework.studentName} | ${homework.postDate?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                val homeworkOnClickListener = {
                    _: View ->
                    val studentNameTextView = TextView(ctx)
                    studentNameTextView.text = "${homework.studentName}"
                    studentNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text = "${homework.postDate?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                    postDateTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    var htmlString = if (homework.text != null) {
                        UIHelper.formatHtml(UIHelper.decodeHtml(homework.text)) } else { "" }
                    val homeworkWebView = UIHelper.generateWebView(ctx, htmlString)
                    listOf(studentNameTextView, homeworkWebView, postDateTextView)
                }
                val homeworkButton = UIHelper.generateButton(ctx, text, homeworkOnClickListener, showDetails, hideDetails, detailsLL)
                homeworkHolder?.addView(homeworkButton)
            }
        }
        fun generateTeacherHomework(ctx: Context, homeworkList: List<TeacherHomework>, homeworkHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (homework in homeworkList) {
                val text =
                    "${homework.poster} | ${homework.deadline?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                val homeworkOnClickListener = {
                    _: View ->
                    val posterTextView = TextView(ctx)
                    posterTextView.text = "${homework.poster}"
                    posterTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    var htmlString = if (homework.text != null) {
                        UIHelper.formatHtml(UIHelper.decodeHtml(homework.text)) } else { "" }
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text =
                        "${homework.postDate?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                    postDateTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    val homeworkWebView = UIHelper.generateWebView(ctx, htmlString)
                    listOf(posterTextView, homeworkWebView, postDateTextView)
                }
                val homeworkButton = UIHelper.generateButton(ctx, text, homeworkOnClickListener, showDetails, hideDetails, detailsLL)
                homeworkHolder?.addView(homeworkButton)
            }
        }
    }
}