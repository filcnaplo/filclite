package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.message.Receiver
import com.thegergo02.minkreta.kreta.data.sub.Type
import org.json.JSONObject

interface MessageView {
    fun displayError(error: String)
    fun displaySuccess(success: String)
    fun backToMain()
    fun generateReceiverList(receivers: List<Receiver>)
    fun generateSendableReceiverTypes(types: List<Type>)
}