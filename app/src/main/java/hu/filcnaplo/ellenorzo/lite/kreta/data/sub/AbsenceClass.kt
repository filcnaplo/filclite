package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
data class AbsenceClass (
    @Embedded(prefix = "startdate_") @Json(name = "KezdoDatum")  val startDate: KretaDate,
    @Embedded(prefix = "enddate_") @Json(name = "VegDatum")  val endDate: KretaDate,
    @Json(name = "Oraszam")  val classCount: Int
)