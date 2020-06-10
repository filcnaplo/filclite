package com.thegergo02.filclite.view

import com.thegergo02.filclite.kreta.data.message.Attachment
import com.thegergo02.filclite.kreta.data.message.Receiver
import com.thegergo02.filclite.kreta.data.sub.Type

interface MessageView {
    fun displayError(error: String)
    fun displaySuccess(success: String)
    fun backToMain()
    fun generateReceiverList(receivers: List<Receiver>)
    fun generateSendableReceiverTypes(types: List<Type>)
    fun assignAttachmentTemporaryId(attachment: Attachment)
}