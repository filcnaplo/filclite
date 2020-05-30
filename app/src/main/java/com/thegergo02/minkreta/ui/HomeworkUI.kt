package com.thegergo02.minkreta.ui

import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
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
import java.time.LocalDateTime

class HomeworkUI {
    companion object {
        private fun getColorFromDeadline(homework: Homework): Int {
            val now = KretaDate(LocalDateTime.now())
            return if (homework.deadlineDate.year == now.year && homework.deadlineDate.month == now.month && homework.deadlineDate.day == now.day) {
                R.color.colorHomeworkAlmostLate
            } else if (homework.deadlineDate > now) {
                R.color.colorHomeworkLate
            } else {
                R.color.colorHomeworkNotLate
            }
        }
        fun generateHomeworkList(ctx: Context,
                                 homeworks: List<Homework>,
                                 homeworkHolder: LinearLayout?,
                                 detailsLL: LinearLayout,
                                 showDetails: () -> Unit,
                                 hideDetails: () -> Unit,
                                 getHomeworkCommentListResult: (String) -> Unit,
                                 sendHomeworkComment: (String, String) -> Unit,
                                 buttonSelectedStyle: Int,
                                 getColorFromAttr: (Int, TypedValue, Boolean) -> Int) {
            for (homework in homeworks) {
                val text = homework.toString()
                val homeworkOnClickListener = {
                    _: View ->
                    val posterTextView = TextView(ctx)
                    posterTextView.text = homework.teacher
                    val htmlString = UIHelper.formatHtml(UIHelper.decodeHtml(homework.text), getColorFromAttr(R.attr.colorBackground, TypedValue(), true), getColorFromAttr(R.attr.colorText, TypedValue(), true))
                    val postDateTextView = TextView(ctx)
                    postDateTextView.text =
                        "${homework.postDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}-${homework.deadlineDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
                    postDateTextView.setTextColor(ContextCompat.getColor(ctx, getColorFromDeadline(homework)))
                    val homeworkWebView = UIHelper.generateWebView(ctx, htmlString)
                    val homeworkEditText = EditText(ctx)
                    homeworkEditText.height = 100
                    homeworkEditText.hint = "Comment on homework"
                    homeworkEditText.setBackgroundColor(getColorFromAttr(R.attr.colorBackground, TypedValue(), true))
                    val homeworkCommentOnListener = {
                        _: View ->
                        sendHomeworkComment(homework.uid, homeworkEditText.text.toString())
                        homeworkEditText.text = null
                        listOf<View>()
                    }
                    val homeworkCommentButton = UIHelper.generateButton(ctx, "SEND", homeworkCommentOnListener, {}, {}, detailsLL, buttonSelectedStyle)
                    getHomeworkCommentListResult(homework.uid)
                    listOf(posterTextView, homeworkWebView, postDateTextView, homeworkEditText, homeworkCommentButton)
                }
                val homeworkButton = UIHelper.generateButton(ctx, text, homeworkOnClickListener, showDetails, hideDetails, detailsLL)
                homeworkButton.setTextColor(ctx.getColor(getColorFromDeadline(homework)))
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
                val contentTextView = TextView(ctx)
                contentTextView.text = comment.text
                contentTextView.paintFlags += if (comment.isStudentDeleted || comment.isTeacherDeleted) Paint.STRIKE_THRU_TEXT_FLAG else 0
                val dateTextView = TextView(ctx)
                dateTextView.text = comment.postDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
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