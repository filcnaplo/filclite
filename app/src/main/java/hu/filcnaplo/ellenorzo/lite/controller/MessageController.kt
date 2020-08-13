package hu.filcnaplo.ellenorzo.lite.controller

import android.content.Context
import hu.filcnaplo.ellenorzo.lite.kreta.KretaError
import hu.filcnaplo.ellenorzo.lite.kreta.KretaRequests
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Attachment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Receiver
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Worker
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Type
import hu.filcnaplo.ellenorzo.lite.view.MessageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageController(ctx: Context, private val messageView: MessageView?, accessToken: String, refreshToken: String, instituteCode: String)
    : KretaRequests.OnSendMessageResult,
    KretaRequests.OnRefreshTokensResult,
    KretaRequests.OnWorkersResult,
    KretaRequests.OnSendableReceiverTypesResult,
    KretaRequests.OnUploadTemporaryAttachmentResult {

    private val apiHandler = KretaRequests(ctx, this, accessToken, refreshToken, instituteCode)

    fun sendMessage(receivers: List<Receiver>, attachments: List<Attachment>, subject: String, content: String, replyId: Int? = null) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.sendMessage(parentListener, receivers, attachments, subject, content, replyId)
        }
    }

    fun getReceivers(type: KretaRequests.ReceiverType) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getReceivers(parentListener, type)
        }
    }

    fun getSendableReceiverTypes() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getSendableReceiverTypes(parentListener)
        }
    }

    fun uploadTemporaryAttachment(attachment: Attachment) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.uploadTemporaryAttachment(parentListener, attachment)
        }
    }

    override fun onSendMessageSuccess() {
        messageView?.displaySuccess("Message sent!")
        messageView?.backToMain()
    }

    override fun onSendMessageError(error: KretaError) {
        messageView?.displayError(ControllerHelper.getErrorString(error.reason, ControllerHelper.ControllerOrigin.Message, ControllerHelper.RequestOrigin.SendMessage))
    }

    override fun onRefreshTokensSuccess(tokens: Map<String, String>) {}
    override fun onRefreshTokensError(error: KretaError) {
        messageView?.displayError(ControllerHelper.getErrorString(error.reason, ControllerHelper.ControllerOrigin.Message, ControllerHelper.RequestOrigin.RefreshToken))
    }

    override fun onWorkersSuccess(workers: List<Worker>) {
        var receivers = mutableListOf<Receiver>()
        for (teacher in workers) {
            receivers.add(teacher.toReceiver())
        }
        messageView?.generateReceiverList(receivers)
    }
    override fun onWorkersError(error: KretaError) {
        messageView?.displayError(ControllerHelper.getErrorString(error.reason, ControllerHelper.ControllerOrigin.Message, ControllerHelper.RequestOrigin.Workers))
    }

    override fun onSendableReceiverTypesSuccess(types: List<Type>) {
        messageView?.generateSendableReceiverTypes(types)
    }
    override fun onSendableReceiverTypesError(error: KretaError) {
        messageView?.displayError(ControllerHelper.getErrorString(error.reason, ControllerHelper.ControllerOrigin.Message, ControllerHelper.RequestOrigin.SendableReceiverTypes))
    }

    override fun onUploadTemporaryAttachmentSuccess(attachment: Attachment) {
        messageView?.assignAttachmentTemporaryId(attachment)
    }
    override fun onUploadTemporaryAttachmentError(error: KretaError) {
        messageView?.displayError(ControllerHelper.getErrorString(error.reason, ControllerHelper.ControllerOrigin.Message, ControllerHelper.RequestOrigin.UploadTemporaryAttachment))
    }
}
