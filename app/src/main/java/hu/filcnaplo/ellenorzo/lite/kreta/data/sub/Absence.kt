@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
@Entity(tableName = "absences")
class Absence (
    @PrimaryKey @Json(name = "Uid") val uid: String,
    @Json(name = "IgazolasAllapota") val justificationState: String,
    @Embedded(prefix = "justtype_") @Json(name = "IgazolasTipusa") val justificationType: Nature,
    @Json(name = "KesesPercben") val delay: Int?,
    @Embedded(prefix = "creatingdate_") @Json(name = "KeszitesDatuma") val creatingDate: KretaDate,
    @Embedded(prefix = "mode_") @Json(name = "Mod") val mode: Nature,
    @Embedded(prefix = "date_") @Json(name = "Datum") val date: KretaDate,
    @Embedded(prefix = "absenceclass_") @Json(name = "Ora") val absenceClass: AbsenceClass,
    @Json(name = "RogzitoTanarNeve") val teacher: String,
    @Embedded(prefix = "subject_") @Json(name = "Tantargy") val subject: Subject,
    @Embedded(prefix = "type_") @Json(name = "Tipus") val type: Nature,
    @Embedded(prefix = "classgroup_") @Json(name = "OsztalyCsoport") val classGroup: ClassGroup
): Comparable<Absence> {
    enum class SortType(val lambda: (it: Absence) -> Comparable<*>) {
        Date({it.date}),
        ClassStartDate({it.absenceClass.startDate}),
        JustificationState({it.justificationState}),
        Teacher({it.teacher}),
        Subject({it.subject.name}),
    }

    override fun toString(): String {
        return "$subject ($teacher) \n" +
                absenceClass.startDate.toFormattedString(KretaDate.KretaDateFormat.DATE)
    }
    fun toDetailedString(): String {
        return  "${type.description} \n" +
                "$subject ($teacher) \n" +
                "$justificationState (${justificationType.description}) \n" +
                "${absenceClass.startDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n" +
                date.toFormattedString(KretaDate.KretaDateFormat.DATE)
    }

    override fun compareTo(other: Absence): Int {
        return this.date.compareTo(other.date)
    }
}