package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
@Entity(tableName = "evals")
class Evaluation(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "eval_uid") @Json(name = "Uid")  val uid: String,
    @Embedded(prefix = "nature_mode_") @Json(name = "Mod") val mode: Nature?,
    @Json(name = "SulySzazalekErteke") val weight: Int?,
    @Json(name = "SzovegesErtek") val textValue: String?,
    @Json(name = "SzovegesErtekRovidNev") val textValueShort: String?,
    @Json(name = "SzamErtek") val numberValue: Int?,
    @Embedded(prefix = "kretadate_seen_") @Json(name = "LattamozasDatuma") val seen: KretaDate?,
    @Embedded(prefix = "kretadate_creatingdate_") @Json(name = "KeszitesDatuma") val creatingDate: KretaDate,
    @Embedded(prefix = "kretadate_postdate_") @Json(name = "RogzitesDatuma") val postDate: KretaDate,
    @Json(name = "Jelleg") val nature: String?,
    @Embedded(prefix = "nature_valuetype_") @Json(name = "ErtekFajta") val valueType: Nature?,
    @Embedded(prefix = "nature_type_") @Json(name = "Tipus") val type: Nature?,
    @Embedded(prefix = "subject_") @Json(name = "Tantargy") val subject: Subject,
    @Json(name = "ErtekeloTanarNeve") val teacher: String,
    @Json(name = "Tema") val theme: String?//,
    //@Json(name = "OsztalyCsoport") val classGroup: String?
): Comparable<Evaluation> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Creating time" to SortType.CreatingDate,
                "Form" to SortType.Nature,
                "Value" to SortType.TextValue,
                "Mode" to SortType.Mode,
                "Subject" to SortType.Subject,
                "Teacher" to SortType.Teacher
            )
            return stringToSortType[str] ?: SortType.CreatingDate
        }
    }

    enum class SortType(val lambda: (it: Evaluation) -> Comparable<*>) {
        CreatingDate({it.creatingDate}),
        Nature({it.nature ?: ""}),
        TextValue({it.textValue ?: ""}),
        Mode({it.mode?.name ?: ""}),
        Subject({it.subject.name}),
        Teacher({it.teacher})
    }

    override fun toString(): String {
        if (nature == "Magatartas" || nature == "Szorgalom") {
            return  "${subject.name} ($teacher) \n" +
                    "$textValue \n" +
                    creatingDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
        }
        var text = "${subject.name} ($teacher)\n" +
                "$textValue"
        if (weight != null) {
            text += " ($weight%)"
        }
        text += "\n"
        if (theme != null) {
            text += "$theme \n"
        }
        text += creatingDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
        return text
    }
    fun toDetailedString(): String {
        var text = "${subject.name} ($teacher)\n" +
                "$textValue"
        if (weight != null) {
            text += " ($weight%)"
        }
        text += "\n"
        if (theme != null) {
            text += "$theme \n"
        }
        text += creatingDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)
        return text
    }
    override fun compareTo(other: Evaluation): Int {
        return this.creatingDate.compareTo(other.creatingDate)
    }
}