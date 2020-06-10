package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Notice (
    @Json(name = "Uid") val uid: String,
    @Json(name = "Cim") val title: String,
    @Json(name = "Tartalom") val text: String,
    @Json(name = "ErvenyessegKezdete") val validFrom: KretaDate,
    @Json(name = "ErvenyessegVege") val validUntil: KretaDate
): Comparable<Notice> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Valid from" to SortType.ValidFrom,
                "Valid until" to SortType.ValidUntil,
                "Title" to SortType.Title)
            return stringToSortType[str] ?: SortType.ValidFrom
        }
    }

    enum class SortType(val lambda: (it: Notice) -> Comparable<*>) {
        ValidFrom({it.validFrom}),
        ValidUntil({it.validUntil}),
        Title({it.title})
    }

    override fun toString(): String {
        return  "$title \n" +
                "${validFrom.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}-${validUntil.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
    fun toDetailedString(): String {
        return  "$title \n" +
                "$text \n" +
                "${validFrom.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}-${validUntil.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
    override fun compareTo(other: Notice): Int {
        return this.validFrom.compareTo(other.validFrom)
    }
}