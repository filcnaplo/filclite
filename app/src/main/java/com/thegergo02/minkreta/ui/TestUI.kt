package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.timetable.Test

class TestUI {
    companion object {
        fun generateTests(
            ctx: Context,
            tests: List<Test>,
            testsHolder: LinearLayout?,
            detailLL: LinearLayout,
            showDetails: () -> Unit,
            hideDetails: () -> Unit
        ) {
            for (test in tests) {
                val testButton = Button(ctx)
                testButton.text = "${test.subject} | ${test.date} | ${test.teacher}"
                testButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                testButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                testButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                testButton.setOnClickListener {
                    hideDetails()
                    val testDetailsTextView = TextView(ctx)
                    testDetailsTextView.text = "${test.subject} (${test.teacher}) \n" +
                            "${test.name} (${test.mode}) \n" +
                            "Date: ${test.date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n" +
                            "Notification Date: ${test.notificationDate?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
                    testDetailsTextView.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.colorText
                        )
                    )
                    detailLL.addView(testDetailsTextView)
                    showDetails()
                }
                testsHolder?.addView(testButton)
            }
        }
    }
}
