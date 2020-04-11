package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
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
            detailsLL: LinearLayout,
            showDetails: () -> Unit,
            hideDetails: () -> Unit
        ) {
            for (test in tests) {
                val text = "${test.subject} | ${test.date} | ${test.teacher}"
                val testOnClickListener = {
                    _: View ->
                    val testDetailsTextView = TextView(ctx)
                    testDetailsTextView.text = "${test.subject} (${test.teacher}) \n" +
                            "${test.name} (${test.mode}) \n" +
                            "${test.date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n"
                    testDetailsTextView.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.colorText
                        )
                    )
                    listOf(testDetailsTextView)
                }
                testsHolder?.removeAllViews()
                val testButton = UIHelper.generateButton(ctx, text, testOnClickListener, showDetails, hideDetails, detailsLL)
                testsHolder?.addView(testButton)
            }
        }
    }
}
