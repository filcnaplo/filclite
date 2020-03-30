package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student
import kotlinx.android.synthetic.main.activity_main.*

class EvaluationUI {
    companion object {
        private fun getColorFromGrade(ctx: Context, grade: Int?): Int {
            when (grade) {
                1 -> return ContextCompat.getColor(ctx, R.color.colorOne)
                2 -> return ContextCompat.getColor(ctx, R.color.colorTwo)
                3 -> return ContextCompat.getColor(ctx, R.color.colorThree)
                4 -> return ContextCompat.getColor(ctx, R.color.colorFour)
                5 -> return ContextCompat.getColor(ctx, R.color.colorFive)
                else -> return ContextCompat.getColor(ctx, R.color.colorText)
            }
        }

        fun generateEvaluations(ctx: Context, cachedStudent: Student, eval_holder_ll: LinearLayout) {
            val currentEvalHolderLinearLayout = LinearLayout(ctx)
            currentEvalHolderLinearLayout.orientation = LinearLayout.VERTICAL
            for (eval in cachedStudent.evaluations) {
                val evalButton = Button(ctx)
                if (eval.form == "Diligence" || eval.form == "Deportment") {
                    evalButton.text = "${eval.value} | ${eval.natureName}"
                } else {
                    evalButton.text =
                        "${eval.value} | ${eval.subject} | ${eval.theme} (${eval.weight})"
                }
                evalButton.setTextColor(getColorFromGrade(ctx, eval.numberValue))
                evalButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                evalButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                currentEvalHolderLinearLayout.addView(evalButton)
            }
            eval_holder_ll.addView(currentEvalHolderLinearLayout)
            eval_holder_ll.visibility = View.GONE
        }
    }
}