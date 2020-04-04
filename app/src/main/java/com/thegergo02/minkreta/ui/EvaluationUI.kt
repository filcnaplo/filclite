package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student

class EvaluationUI {
    companion object {
        private fun getColorFromGrade(ctx: Context, grade: Int?): Int {
            return when (grade) {
                1 -> ContextCompat.getColor(ctx, R.color.colorOne)
                2 -> ContextCompat.getColor(ctx, R.color.colorTwo)
                3 -> ContextCompat.getColor(ctx, R.color.colorThree)
                4 -> ContextCompat.getColor(ctx, R.color.colorFour)
                5 -> ContextCompat.getColor(ctx, R.color.colorFive)
                else -> ContextCompat.getColor(ctx, R.color.colorText)
            }
        }

        fun generateEvaluations(ctx: Context, cachedStudent: Student, eval_holder_ll: LinearLayout?, details_ll: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
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
                evalButton.setOnClickListener {
                    hideDetails()
                    val evalDetailsTextView = TextView(ctx)
                    evalDetailsTextView.text = "ID: ${eval.id} \n" +
                            "Form: ${eval.form} \n" +
                            "FormName: ${eval.formName} \n" +
                            "Type: ${eval.type} \n" +
                            "TypeName: ${eval.typeName} \n" +
                            "Subject: ${eval.subject} \n" +
                            "SubjectCategory: ${eval.subjectCategory} \n" +
                            "SubjectCategoryName: ${eval.subjectCategoryName} \n" +
                            "Theme: ${eval.theme} \n" +
                            "Does it count into average: ${eval.countsIntoAverage} \n" +
                            "Mode: ${eval.mode} \n" +
                            "Weight: ${eval.weight} \n" +
                            "Value: ${eval.value} \n" +
                            "NumberValue: ${eval.numberValue} \n" +
                            "Tutelary seen it at (UTC): ${eval.seenByTutelaryUtc} \n" +
                            "Teacher: ${eval.teacher} \n" +
                            "Date: ${eval.date} \n" +
                            "CreatingTime: ${eval.creatingTime} \n" +
                            "Nature: ${eval.nature} \n" +
                            "NatureName: ${eval.natureName} \n" +
                            "ValueType: ${eval.valueType} \n" +
                            "ClassGroupUid: ${eval.classGroupUid}"
                    evalDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    details_ll.addView(evalDetailsTextView)
                    showDetails()
                }
                eval_holder_ll?.addView(evalButton)
            }
        }
    }
}