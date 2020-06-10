package hu.filcnaplo.ellenorzo.lite.view

import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Attachment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Receiver
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Type

interface MessageView {
    fun displayError(error: String)
    fun displaySuccess(success: String)
    fun backToMain()
    fun generateReceiverList(receivers: List<Receiver>)
    fun generateSendableReceiverTypes(types: List<Type>)
    fun assignAttachmentTemporaryId(attachment: Attachment)
}