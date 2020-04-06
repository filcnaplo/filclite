package com.thegergo02.minkreta.data.message

import com.squareup.moshi.Json
import com.thegergo02.minkreta.KretaDate

data class Message(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "kuldesDatum") val sendDate: KretaDate,
    @Json(name = "feladoNev") val senderName: String?,
    @Json(name = "feladoTitulus") val senderRole: String?,
    @Json(name = "szoveg") val text: String?,
    @Json(name = "targy") val subject: String?,
    @Json(name = "cimzettLista") val receiverList: List<Receiver>,
    @Json(name = "csatolmanyok") val attachments: List<String>
)
