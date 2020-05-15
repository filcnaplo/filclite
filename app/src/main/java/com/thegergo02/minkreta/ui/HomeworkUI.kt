package com.thegergo02.minkreta.ui

import android.content.Context
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.homework.HomeworkComment

class HomeworkUI {
    companion object {
        fun generateHomeworkList(ctx: Context,
                                 homeworks: List<Homework>,
                                 homeworkHolder: LinearLayout?,
                                 detailsLL: LinearLayout,
                                 showDetails: () -> Unit,
                                 hideDetails: () -> Unit,
                                 getHomeworkCommentListResult: (String) -> Unit,
                                 sendHomeworkComment: (String, String) -> Unit) {
            for (homework in homeworks) {
                val text = homework.toString()
                val homeworkOnClickListener = {
                    _: View ->
                    val posterTextView = TextView(ctx)
                    posterTextView.text = homework.teacher
                    posterTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    val htmlString = UIHelper.formatHtml(UIHelper.decodeHtml(homework.text))
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text =
                        "${homework.postDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}-${homework.deadlineDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                    postDateTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    val homeworkWebView = UIHelper.generateWebView(ctx, htmlString)
                    val homeworkEditText = EditText(ctx)
                    homeworkEditText.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    homeworkEditText.height = 100
                    homeworkEditText.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                    homeworkEditText.setHintTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    homeworkEditText.hint = "Comment on homework"
                    val homeworkCommentOnListener = {
                        _: View ->
                        sendHomeworkComment(homework.uid, homeworkEditText.text.toString())
                        homeworkEditText.text = null
                        listOf<View>()
                    }
                    val homeworkCommentButton = UIHelper.generateButton(ctx, "SEND", homeworkCommentOnListener, {}, {}, detailsLL, R.color.colorText, R.color.colorAccent)
                    getHomeworkCommentListResult(homework.uid)
                    listOf(posterTextView, homeworkWebView, postDateTextView, homeworkEditText, homeworkCommentButton)
                }
                val homeworkButton = UIHelper.generateButton(ctx, text, homeworkOnClickListener, showDetails, hideDetails, detailsLL)
                homeworkHolder?.addView(homeworkButton)
            }
        }
        fun generateHomeworkCommentList(ctx: Context, comments: List<HomeworkComment>, detailsLL: LinearLayout, homeworkCommentHolder: LinearLayout): LinearLayout {
            homeworkCommentHolder.removeAllViews()
            val commentHolder = LinearLayout(ctx)
            commentHolder.orientation = LinearLayout.VERTICAL
            for (comment in comments) {
                val posterTextView = TextView(ctx)
                posterTextView.text = comment.author
                posterTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                val contentTextView = TextView(ctx)
                contentTextView.text = comment.text
                contentTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                contentTextView.paintFlags += if (comment.isStudentDeleted || comment.isTeacherDeleted) Paint.STRIKE_THRU_TEXT_FLAG else 0
                val dateTextView = TextView(ctx)
                dateTextView.text = comment.postDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
                dateTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                dateTextView.gravity = Gravity.RIGHT
                commentHolder.addView(posterTextView)
                commentHolder.addView(contentTextView)
                commentHolder.addView(dateTextView)
            }
            detailsLL.addView(commentHolder)
            return commentHolder
        }
    }
}