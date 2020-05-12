package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.homework.Homework

class HomeworkUI {
    companion object {
        fun generateHomeworkList(ctx: Context, homeworks: List<Homework>, homeworkHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (homework in homeworks) {
                val text = homework.toString()
                val homeworkOnClickListener = {
                    _: View ->
                    val posterTextView = TextView(ctx)
                    posterTextView.text = "${homework.teacher}"
                    posterTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    var htmlString = if (homework.text != null) {
                        UIHelper.formatHtml(UIHelper.decodeHtml(homework.text)) } else { "" }
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text =
                        "${homework.postDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}-${homework.deadlineDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
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