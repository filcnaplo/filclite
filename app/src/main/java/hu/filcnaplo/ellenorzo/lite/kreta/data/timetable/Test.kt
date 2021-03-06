package hu.filcnaplo.ellenorzo.lite.kreta.data.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.ClassGroup
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Nature

@JsonClass(generateAdapter = true)
class Test(
    @Json(name = "Uid")  val uid: String,
    @Json(name = "Datum")  val date: KretaDate,
    @Json(name = "Modja")  val mode: Nature,
    @Json(name = "TantargyNeve")  val subject: String,
    @Json(name = "RogzitoTanarNeve")  val teacher: String,
    @Json(name = "BejelentesDatuma")  val notificationDate: KretaDate,
    @Json(name = "OrarendiOraOraszama")  val classNumber: Int,
    @Json(name = "Temaja")  val theme: String?,
    @Json(name = "OsztalyCsoport")  val classGroup: ClassGroup
) {
    override fun toString(): String {
        return  "$subject ($teacher) \n" +
                "$theme (${mode.description})\n" +
                "${date?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
    }
    fun toDetailedString(): String {
        return  "$subject ($teacher) \n" +
                "$theme (${mode.description})\n" +
                "${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
}
