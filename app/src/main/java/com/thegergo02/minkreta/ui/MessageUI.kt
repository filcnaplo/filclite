package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.message.Message
import com.thegergo02.minkreta.data.message.MessageDescriptor

class MessageUI {
    companion object {
        fun generateMessage(
            ctx: Context,
            message: Message,
            detailsLL: LinearLayout,
            showDetails: () -> Unit,
            hideDetails: () -> Unit
        ) {
            UIHelper.wrapIntoDetails({
                val subjectTextView = TextView(ctx)
                subjectTextView.text = "${message.subject}"
                subjectTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                val senderTextView = TextView(ctx)
                senderTextView.text = "${message.senderName} (${message.senderRole}) \n" +
                        "${message.sendDate?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
                senderTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                val htmlString = if (message.text != null) {
                    UIHelper.formatHtml(UIHelper.decodeHtml(message.text))
                } else { "" }
                val messageWebView = UIHelper.generateWebView(ctx, htmlString)
                listOf(subjectTextView, messageWebView, senderTextView)
            }, showDetails, hideDetails, detailsLL)(View(ctx))
        }

        fun generateMessageDescriptors(
            ctx: Context,
            messageDescriptors: List<MessageDescriptor>,
            messageDescriptorsHolder: LinearLayout?,
            controller: MainController,
            accessToken: String
        ) {
            messageDescriptorsHolder?.removeAllViews()
            for (messageDescriptor in messageDescriptors) {
                val message = messageDescriptor.message
                val text =
                    "${message.subject} | ${message.senderName} (${message.senderRole})"
                val textColor = if (messageDescriptor.isRead) {
                    R.color.colorUnavailable
                } else {
                    R.color.colorText
                }
                val messageOnClickListener = {
                    _: View ->
                    if (message.id != null) {
                        controller.getMessage(accessToken, messageDescriptor.id)
                        controller.setMessageRead(accessToken, message.id)
                    }
                    null
                }
                val messageButton = UIHelper.generateButton(ctx, text, messageOnClickListener, {}, {}, LinearLayout(ctx), textColor)
                messageDescriptorsHolder?.addView(messageButton)
            }
        }
    }
}