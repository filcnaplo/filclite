package com.thegergo02.minkreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageDescriptor (
    @Json(name = "azonosito") val id: Int,
    @Json(name = "isElolvasva") val isRead: Boolean,
    @Json(name = "isToroltElem") val isDeleted: Boolean,
    @Json(name = "tipus") val type: MessageType,
    @Json(name = "uzenet") val message: Message
)
