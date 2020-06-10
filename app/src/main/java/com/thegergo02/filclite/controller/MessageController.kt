package com.thegergo02.filclite.controller

import android.content.Context
import com.thegergo02.filclite.kreta.KretaError
import com.thegergo02.filclite.kreta.KretaRequests
import com.thegergo02.filclite.kreta.data.message.Attachment
import com.thegergo02.filclite.kreta.data.message.Receiver
import com.thegergo02.filclite.kreta.data.message.Worker
import com.thegergo02.filclite.kreta.data.sub.Type
import com.thegergo02.filclite.view.MessageView
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
        messageView?.displayError(error.errorString)
    }

    override fun onRefreshTokensSuccess(tokens: Map<String, String>) {}
    override fun onRefreshTokensError(error: KretaError) {
        messageView?.displayError(error.errorString)
    }

    override fun onWorkersSuccess(workers: List<Worker>) {
        var receivers = mutableListOf<Receiver>()
        for (teacher in workers) {
            receivers.add(teacher.toReceiver())
        }
        messageView?.generateReceiverList(receivers)
    }
    override fun onWorkersError(error: KretaError) {
        messageView?.displayError(error.errorString)
    }

    override fun onSendableReceiverTypesSuccess(types: List<Type>) {
        messageView?.generateSendableReceiverTypes(types)
    }
    override fun onSendableReceiverTypesError(error: KretaError) {
        messageView?.displayError(error.errorString)
    }

    override fun onUploadTemporaryAttachmentSuccess(attachment: Attachment) {
        messageView?.assignAttachmentTemporaryId(attachment)
    }
    override fun onUploadTemporaryAttachmentError(error: KretaError) {
        messageView?.displayError(error.errorString)
    }
}
