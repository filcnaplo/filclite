package hu.filcnaplo.ellenorzo.lite.kreta.data.message

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
@Entity(tableName = "messages")
class MessageDescriptor (
    @PrimaryKey(autoGenerate = false) @Json(name = "azonosito") val id: Int,
    @Json(name = "uzenetAzonosito") val messageId: Int,
    @Embedded @Json(name = "uzenetKuldesDatum") val date: KretaDate,
    @Json(name = "uzenetFeladoNev") val sender: String? = "",
    @Json(name = "uzenetFeladoTitulus") val role: String?,
    @Json(name = "uzenetTargy") val subject: String,
    @Json(name = "isElolvasva") val isRead: Boolean,
    @Json(name = "hasCsatolmany") val hasAttachment: Boolean,
    var type: String? = null
): Comparable<MessageDescriptor> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Teacher" to SortType.Teacher,
                "Send date" to SortType.SendDate)
            return stringToSortType[str] ?: SortType.SendDate
        }
    }

    class MessageDescriptorTypeConverter {
        @androidx.room.TypeConverter
        fun fromStringToType(str: String): Type {
            return when (str) {
                Type.Inbox.toString() -> Type.Inbox
                Type.Sent.toString() -> Type.Sent
                Type.Trash.toString() -> Type.Trash
                else -> Type.Inbox
            }
        }

        @androidx.room.TypeConverter
        fun fromTypeToString(type: Type): String {
            return type.toString()
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
