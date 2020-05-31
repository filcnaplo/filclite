package com.thegergo02.minkreta.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.data.sub.Note

@JsonClass(generateAdapter = true)
class MessageDescriptor (
    @Json(name = "azonosito") val id: Int,
    @Json(name = "isElolvasva") val isRead: Boolean,
    @Json(name = "isToroltElem") val isDeleted: Boolean,
    @Json(name = "tipus") val type: MessageType,
    @Json(name = "uzenet") val message: Message
): Comparable<MessageDescriptor> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Teacher" to SortType.Teacher,
                "Send date" to SortType.SendDate)
            return stringToSortType[str] ?: SortType.SendDate
        }
    }

    enum class Type(val endpoint: String) {
        All("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/sajat"),
        Inbox("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/beerkezett"),
        Sent("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/elkuldott"),
        Trash("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/torolt")
    }

    enum class SortType(val lambda: (it: MessageDescriptor) -> Comparable<*>) {
        SendDate({it.message.sendDate}),
        Teacher({it.message.senderName})
    }
    override fun compareTo(other: MessageDescriptor): Int {
        return this.message.sendDate.compareTo(other.message.sendDate)
    }
}
