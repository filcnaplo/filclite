package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.Student

class EvaluationUI {
    companion object {
        private fun getColorFromGrade(ctx: Context, grade: Int?): Int {
            return when (grade) {
                1 -> R.color.colorOne
                2 -> R.color.colorTwo
                3 -> R.color.colorThree
                4 -> R.color.colorFour
                5 -> R.color.colorFive
                else -> R.color.colorText
            }
        }

        fun generateEvaluations(ctx: Context, cachedStudent: Student, eval_holder_ll: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            if (cachedStudent.evaluations != null) {
                for (eval in cachedStudent.evaluations) {
                    val evalOnClickListener = {
                        _: View ->
                        val evalDetailsTextView = TextView(ctx)
                        evalDetailsTextView.text = eval.toDetailedString()
                        evalDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                        listOf(evalDetailsTextView)
                    }
                    val evalButton = UIHelper.generateButton(ctx, eval.toString(), evalOnClickListener, showDetails, hideDetails, detailsLL, getColorFromGrade(ctx, eval.numberValue))
                    eval_holder_ll?.addView(evalButton)
                }
            }
        }
    }
}