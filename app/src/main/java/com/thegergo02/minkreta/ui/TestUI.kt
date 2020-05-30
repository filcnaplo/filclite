package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.timetable.Test

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
                val text = test.toString()
                val testOnClickListener = {
                    _: View ->
                    val testDetailsTextView = TextView(ctx)
                    testDetailsTextView.text = test.toDetailedString()
                    listOf(testDetailsTextView)
                }
                testsHolder?.removeAllViews()
                val testButton = UIHelper.generateButton(ctx, text, testOnClickListener, showDetails, hideDetails, detailsLL)
                testsHolder?.addView(testButton)
            }
        }
    }
}
