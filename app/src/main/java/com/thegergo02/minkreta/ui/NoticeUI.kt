package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.sub.Notice

class NoticeUI {
    companion object {
        fun generateNoticeList(ctx: Context, noticeList: List<Notice>, noticeHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (notice in noticeList) {
                val text = notice.toString()
                val noticeOnClickListener = {
                    _: View ->
                    val noticeDetailsTextView = TextView(ctx)
                    noticeDetailsTextView.text = notice.toDetailedString()
                    noticeDetailsTextView.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.colorText
                        )
                    )
                    listOf(noticeDetailsTextView)
                }
                val noticeButton = UIHelper.generateButton(ctx, text, noticeOnClickListener, showDetails, hideDetails, detailsLL)
                noticeHolder?.addView(noticeButton)
            }
        }
    }
}