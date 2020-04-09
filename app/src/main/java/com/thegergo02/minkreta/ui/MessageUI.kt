package com.thegergo02.minkreta.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
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
            hideDetails()
            val messageWebView = WebView(ctx)
            val subjectTextView = TextView(ctx)
            subjectTextView.text = "${message.subject}"
            subjectTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
            val cssString = "<style>body{background-color: black !important;color: white;}</style>"
            val htmlMessage =
                    "${cssString}${message.text?.replace("style=\"color: black;\"", "style=\"color: white;\"")?.replace("style=\"color: rgb(0, 0, 0);\"", "style=\"color: white;\"")}"
            Log.w("html", htmlMessage)
            val senderTextView = TextView(ctx)
            senderTextView.text = "${message.senderName} (${message.senderRole}) \n" +
                    "${message.sendDate?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
            senderTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
            messageWebView.loadData(htmlMessage, "text/html", "UTF-8")
            detailsLL.addView(subjectTextView)
            detailsLL.addView(messageWebView)
            detailsLL.addView(senderTextView)
            showDetails()
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
                val messageButton = Button(ctx)
                messageButton.text =
                    "${message.subject} | ${message.senderName} (${message.senderRole})"
                messageButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                val textColor = if (messageDescriptor.isRead) {
                    ContextCompat.getColor(ctx, R.color.colorUnavailable)
                } else {
                    ContextCompat.getColor(ctx, R.color.colorText)
                }
                messageButton.setTextColor(textColor)
                messageButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                messageButton.setOnClickListener {
                    if (message.id != null) {
                        controller.getMessage(accessToken, messageDescriptor.id)
                        controller.setMessageRead(accessToken, message.id)
                    }
                }
                messageDescriptorsHolder?.addView(messageButton)
            }
        }
    }
}