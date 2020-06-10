package hu.filcnaplo.ellenorzo.lite.kreta.data.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.ClassGroup

@JsonClass(generateAdapter = true)
class Homework(
    @Json(name = "Uid")  val uid: String,
    @Json(name = "FeladasDatuma")  val postDate: KretaDate,
    @Json(name = "HataridoDatuma")  val deadlineDate: KretaDate,
    @Json(name = "RogzitesIdopontja")  val pinDate: KretaDate,
    @Json(name = "IsTanarRogzitette")  val teacherHomework: Boolean,
    @Json(name = "IsTanuloHaziFeladatEnabled")  val isStudentHomeworkEnabled: Boolean,
    @Json(name = "RogzitoTanarNeve")  val teacher: String,
    @Json(name = "TantargyNeve")  val subject: String,
    @Json(name = "Szoveg")  val text: String,
    @Json(name = "OsztalyCsoport")  val classGroup: ClassGroup,
    @Json(name = "OpenBoardKepek")  val pictures: List<String>?
): Comparable<Homework> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Post date" to SortType.PostDate,
                "Deadline" to SortType.Deadline,
                "Teacher" to SortType.Teacher,
                "Subject" to SortType.Subject)
            return stringToSortType[str] ?: SortType.PostDate
        }
    }

    enum class SortType(val lambda: (it: Homework) -> Comparable<*>) {
        PostDate({it.postDate}),
        Deadline({it.deadlineDate}),
        Teacher({it.teacher}),
        Subject({it.subject}),
    }

    override fun toString(): String {
        return "$subject ($teacher) \n" +
                "${postDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}-${deadlineDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
    }

    override fun compareTo(other: Homework): Int {
        return this.postDate.compareTo(other.postDate)
    }
}