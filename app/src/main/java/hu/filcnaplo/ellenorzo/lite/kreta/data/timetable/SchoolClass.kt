package hu.filcnaplo.ellenorzo.lite.kreta.data.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.ClassGroup
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Nature
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Subject

@JsonClass(generateAdapter = true)
class SchoolClass(
    @Json(name = "Uid")  val uid: String,
    @Json(name = "Allapot")  val state: Nature,
    @Json(name = "BejelentettSzamonkeresUids")  val testUids: List<String>?,
    @Json(name = "Datum")  val date: KretaDate,
    @Json(name = "HelyettesitoTanarNeve")  val deputyTeacher: String?,
    @Json(name = "IsTanuloHaziFeladatEnabled")  val isStudentHomeworkEnabled: Boolean,
    @Json(name = "KezdetIdopont")  val startDate: KretaDate,
    @Json(name = "Nev")  val name: String,
    @Json(name = "Oraszam")  val count: Int?,
    @Json(name = "OraEvesSorszama")  val yearCount: Int?,
    @Json(name = "OsztalyCsoport")  val classGroup: ClassGroup?,
    @Json(name = "HaziFeladatUid")  val homeworkUid: String?,
    @Json(name = "TanarNeve")  val teacher: String?,
    @Json(name = "Tantargy")  val subject: Subject?,
    @Json(name = "TanuloJelenlet")  val studentPresence: Nature?,
    @Json(name = "IsHaziFeladatMegoldva")  val done: Boolean,
    @Json(name = "Tema")  val theme: String?,
    @Json(name = "TeremNeve")  val classRoom: String?,
    @Json(name = "Tipus")  val type: Nature,
    @Json(name = "VegIdopont")  val endDate: KretaDate,
    var schoolDay: SchoolDay? = null
) {
    override fun toString(): String {
        return  "$count. $subject ($classRoom) \n" +
                "$teacher"
    }
    fun toDetailedString(): String {
        val deputy = if (deputyTeacher != null) {
            "(Deputy: ${deputyTeacher})"
        } else {
            ""
        }
        return  "$subject \n" +
                "${startDate.toFormattedString(KretaDate.KretaDateFormat.TIME)}-${endDate.toFormattedString(
                    KretaDate.KretaDateFormat.TIME)} \n" +
                "$classRoom \n" +
                "$teacher $deputy"
    }
}
