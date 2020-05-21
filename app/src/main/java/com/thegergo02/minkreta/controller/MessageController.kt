package com.thegergo02.minkreta.controller

import android.content.Context
import com.thegergo02.minkreta.kreta.KretaError
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.Receiver
import com.thegergo02.minkreta.kreta.data.sub.Worker
import com.thegergo02.minkreta.view.MessageView

class MessageController(ctx: Context, private val messageView: MessageView?, accessToken: String, refreshToken: String, instituteCode: String)
    : KretaRequests.OnSendMessageResult, KretaRequests.OnRefreshTokensResult, KretaRequests.OnWorkersResult {

    private val apiHandler = KretaRequests(ctx, this, accessToken, refreshToken, instituteCode)

    fun sendMessage(receivers: List<Receiver>, attachments: List<Attachment>, subject: String, content: String, replyId: Int? = null) {
        apiHandler.sendMessage(this, receivers, attachments, subject, content, replyId)
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
}
