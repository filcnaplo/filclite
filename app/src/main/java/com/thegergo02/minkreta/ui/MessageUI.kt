package com.thegergo02.minkreta.ui

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.activity.MessageActivity
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.Message
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor


class MessageUI {
    companion object {
        fun generateMessage(
            ctx: Context,
            message: Message,
            detailsLL: LinearLayout,
            downloadAttachment: (attachment: Attachment) -> Unit,
            showDetails: () -> Unit,
            hideDetails: () -> Unit,
            themeHelper: ThemeHelper
        ) {
            UIHelper.wrapIntoDetails({
                val subjectTextView = TextView(ctx)
                subjectTextView.text = "${message.subject}"
                subjectTextView.setTextColor(themeHelper.getColorFromAttributes(R.attr.colorText))
                val senderTextView = TextView(ctx)
                senderTextView.text = "${message.senderName} (${message.senderRole}) \n" +
                        message.sendDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
                val htmlString = if (message.text != null) {
                    UIHelper.formatHtml(UIHelper.decodeHtml(message.text), themeHelper.getColorFromAttributes(R.attr.colorBackground), themeHelper.getColorFromAttributes(R.attr.colorText))
                } else { "" }
                val messageWebView = UIHelper.generateWebView(ctx, htmlString)
                val attachmentLinearLayout = LinearLayout(ctx)
                for (attachment in message.attachments) {
                    val onAttachmentClickListener = {
                            _: View ->
                        downloadAttachment(attachment)
                        listOf<View>()
                    }
                    val attachmentButton = UIHelper.generateButton(ctx, attachment.fileName, onAttachmentClickListener)
                    attachmentButton.setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorAccent))
                    val params = ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(10, 0, 10, 0)
                    attachmentButton.layoutParams = params
                    attachmentLinearLayout.addView(attachmentButton)
                }
                listOf(subjectTextView, messageWebView, attachmentLinearLayout, senderTextView)
            }, showDetails, hideDetails, detailsLL)(View(ctx))
        }

        fun generateMessageDescriptors(
            ctx: Context,
            messageDescriptors: List<MessageDescriptor>,
            messageDescriptorsHolder: LinearLayout?,
            controller: MainController,
            themeHelper: ThemeHelper
        ) {
            messageDescriptorsHolder?.removeAllViews()
            val sendMessageOnClickListener = {
                _: View ->
                val messageIntent = Intent(ctx, MessageActivity::class.java)
                ctx.startActivity(messageIntent)
                null
            }
            val sendMessageButton = UIHelper.generateButton(ctx, ctx.getString(R.string.send_ma), sendMessageOnClickListener)
            messageDescriptorsHolder?.addView(sendMessageButton)
            for (messageDescriptor in messageDescriptors) {
                val message = messageDescriptor.message
                val text =
                    "${message.subject} | ${message.senderName} (${message.senderRole})"
                val textColor = if (messageDescriptor.isRead) {
                    themeHelper.getColorFromAttributes(R.attr.colorUnavailable)
                } else {
                    themeHelper.getColorFromAttributes(R.attr.colorText)
                }
                val messageOnClickListener = {
                    _: View ->
                    if (message.id != null) {
                        controller.getMessage(messageDescriptor.id)
                        controller.setMessageRead(message.id)
                    }
                    null
                }
                val messageButton = UIHelper.generateButton(ctx, text, messageOnClickListener, {}, {}, LinearLayout(ctx))
                messageButton.setTextColor(textColor)
                messageDescriptorsHolder?.addView(messageButton)
            }
        }
    }
}