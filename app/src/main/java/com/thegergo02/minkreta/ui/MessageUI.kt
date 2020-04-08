package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
            val messageTextView = TextView(ctx)
            messageTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
            messageTextView.text = "Receivers: ${message.receiverList} \n" +
                    "${message.subject} \n" +
                    "${message.text} \n" +
                    "${message.senderName} (${message.senderRole}) \n" +
                    "${message.sendDate} \n" +
                    "Attachments: TODO: IMPLEMENT"
            detailsLL.addView(messageTextView)
        }

        fun generateMessageDescriptors(
            ctx: Context,
            messageDescriptors: List<MessageDescriptor>,
            messageDescriptorsHolder: LinearLayout?,
            controller: MainController,
            accessToken: String
        ) {
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
                        controller.getMessage(accessToken, message.id)
                    }
                }
                messageDescriptorsHolder?.addView(messageButton)
            }
        }
    }
}