package com.thegergo02.filclite.ui

import android.content.Context
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.filclite.kreta.KretaDate
import com.thegergo02.filclite.R
import com.thegergo02.filclite.kreta.data.homework.Homework
import com.thegergo02.filclite.kreta.data.homework.HomeworkComment
import com.thegergo02.filclite.ui.manager.RefreshableData
import java.time.LocalDateTime

class HomeworkUI {
    companion object {
        fun getColorFromDeadline(homework: Homework): Int {
            val now = KretaDate(LocalDateTime.now())
            return if (homework.deadlineDate.year == now.year && homework.deadlineDate.month == now.month && homework.deadlineDate.day == now.day) {
                R.color.colorHomeworkAlmostLate
            } else if (homework.deadlineDate > now) {
                R.color.colorHomeworkLate
            } else {
                R.color.colorHomeworkNotLate
            }

        }
        fun generateHomework(ctx: Context,
                             homework: Homework,
                             themeHelper: ThemeHelper,
                             sendHomeworkComment: (String, String) -> Unit,
                             getHomeworkCommentListResult: (String) -> Unit
        ): List<View> {
            val posterTextView = TextView(ctx)
            posterTextView.text = homework.teacher
            val htmlString = UIHelper.formatHtml(UIHelper.decodeHtml(homework.text),
                themeHelper.getColorFromAttributes(R.attr.colorBackground),
                themeHelper.getColorFromAttributes(R.attr.colorText))
            val postDateTextView = TextView(ctx)
            postDateTextView.text =
                "${homework.postDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}-${homework.deadlineDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
            postDateTextView.setTextColor(ContextCompat.getColor(ctx, getColorFromDeadline(homework)))
            val homeworkWebView = UIHelper.generateWebView(ctx, htmlString)
            val homeworkEditText = EditText(ctx)
            homeworkEditText.height = 100
            homeworkEditText.hint = "Comment on homework"
            homeworkEditText.setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorBackground))
            val homeworkCommentOnListener = {
                    _: View, _: RefreshableData ->
                sendHomeworkComment(homework.uid, homeworkEditText.text.toString())
                homeworkEditText.text = null
                listOf<View>()
            }
            val homeworkCommentButton = UIHelper.generateButton(ctx, "SEND", homeworkCommentOnListener)
            homeworkCommentButton.setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorButtonSelected))
            getHomeworkCommentListResult(homework.uid)
            return listOf(posterTextView, homeworkWebView, postDateTextView, homeworkEditText, homeworkCommentButton)
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