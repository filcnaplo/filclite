package com.thegergo02.minkreta.kreta.data.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Test(
    @Json(name = "Uid")  val uid: String?,
    @Json(name = "Id")  val id: Int?,
    @Json(name = "Datum")  val date: KretaDate?,
    @Json(name = "HetNapja")  val dayOfWeek: String?,
    @Json(name = "Oraszam")  val classNumber: Int?,
    @Json(name = "Tantargy")  val subject: String?,
    @Json(name = "Tanar")  val teacher: String?,
    @Json(name = "SzamonkeresMegnevezese")  val name: String?,
    @Json(name = "SzamonkeresModja")  val mode: String?,
    @Json(name = "BejelentesDatuma")  val notificationDate: KretaDate?,
    @Json(name = "OsztalyCsoportUid")  val classGroupUid: String?
) {
    override fun toString(): String {
        return  "$subject ($teacher) \n" +
                "$name ($mode)\n" +
                "${date?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
    }
    fun toDetailedString(): String {
        return  "$subject ($teacher) \n" +
                "$name ($mode)\n" +
                "${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
}
