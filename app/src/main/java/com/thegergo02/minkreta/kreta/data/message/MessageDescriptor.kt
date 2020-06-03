package com.thegergo02.minkreta.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.adapter.KretaDateAdapter
import com.thegergo02.minkreta.kreta.data.sub.Note

@JsonClass(generateAdapter = true)
class MessageDescriptor (
    @Json(name = "azonosito") val id: Int,
    @Json(name = "uzenetAzonosito") val messageId: Int,
    @Json(name = "uzenetKuldesDatum") val date: KretaDate,
    @Json(name = "uzenetFeladoNev") val sender: String? = "",
    @Json(name = "uzenetFeladoTitulus") val role: String?,
    @Json(name = "uzenetTargy") val subject: String,
    @Json(name = "isElolvasva") val isRead: Boolean,
    @Json(name = "hasCsatolmany") val hasAttachment: Boolean
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
        Inbox("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/beerkezett"),
        Sent("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/elkuldott"),
        Trash("https://eugyintezes.e-kreta.hu/api/v1/kommunikacio/postaladaelemek/torolt")
    }

    enum class SortType(val lambda: (it: MessageDescriptor) -> Comparable<*>) {
        SendDate({it.date}),
        Teacher({it.sender ?: "No Sender"})
    }
    override fun compareTo(other: MessageDescriptor): Int {
        return this.date.compareTo(other.date)
    }

    override fun toString(): String {
        return "$sender \n $subject \n ${date.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
}
