package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
@Entity(tableName = "notes")
class Note (
    @PrimaryKey @Json(name = "Uid") val uid: String,
    @Json(name = "Cim") val title: String,
    @Embedded(prefix = "date_") @Json(name = "Datum") val date: KretaDate,
    @Embedded(prefix = "creatingdate_") @Json(name = "KeszitesDatuma") val creatingDate: KretaDate,
    @Json(name = "KeszitoTanarNeve") val teacher: String,
    @Embedded(prefix = "seendate_") @Json(name = "LattamozasDatuma") val seenDate: KretaDate?,
    @Embedded(prefix = "classgroup_") @Json(name = "OsztalyCsoport") val classGroup: ClassGroup?,
    @Json(name = "Tartalom") val text: String,
    @Embedded(prefix = "nature_") @Json(name = "Tipus") val type: Nature
): Comparable<Note> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Teacher" to SortType.Teacher,
                "Creating time" to SortType.Date,
                "Justification state" to SortType.Type)
            return stringToSortType[str] ?: SortType.Date
        }
    }

    enum class SortType(val lambda: (it: Note) -> Comparable<*>) {
        Date({it.date}),
        Type({it.type.name ?: ""}),
        Teacher({it.teacher})
    }

    override fun toString(): String {
        return  "$title \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATE)})"
    }
    fun toDetailedString(): String {
        return  "$title (${type.description}) \n" +
                "$text \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)})"
    }
    override fun compareTo(other: Note): Int {
        return this.date.compareTo(other.date)
    }
}