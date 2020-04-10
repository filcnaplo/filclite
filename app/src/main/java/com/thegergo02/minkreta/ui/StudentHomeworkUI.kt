package com.thegergo02.minkreta.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.homework.StudentHomework

class StudentHomeworkUI {
    companion object {
        fun generateHomework(ctx: Context, homeworkList: List<StudentHomework>, homeworkHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (homework in homeworkList) {
                val homeworkButton = Button(ctx)
                homeworkButton.text = "${homework.studentName} | ${homework.postDate?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
                homeworkButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                homeworkButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                homeworkButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                homeworkButton.setOnClickListener {
                    hideDetails()
                    val homeworkWebView = WebView(ctx)
                    val studentNameTextView = TextView(ctx)
                    studentNameTextView.text = "${homework.studentName}"
                    studentNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    val cssString = "<style>body{background-color: black !important;color: white;}</style>"
                    val htmlMessage =
                        "${cssString}${homework.text?.replace("style=\"color: black;\"", "style=\"color: white;\"")?.replace("style=\"color: rgb(0, 0, 0);\"", "style=\"color: white;\"")}"
                    Log.w("html", htmlMessage)
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text = "${homework.postDate?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
                    postDateTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    homeworkWebView.loadData(htmlMessage, "text/html", "UTF-8")
                    detailsLL.addView(studentNameTextView)
                    detailsLL.addView(homeworkWebView)
                    detailsLL.addView(postDateTextView)
                    showDetails()
                }
                homeworkHolder?.addView(homeworkButton)
            }
        }
    }
}