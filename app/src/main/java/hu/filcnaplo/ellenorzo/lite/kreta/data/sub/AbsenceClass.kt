package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

@JsonClass(generateAdapter = true)
data class AbsenceClass (
    @Json(name = "KezdoDatum")  val startDate: KretaDate,
    @Json(name = "VegDatum")  val endDate: KretaDate,
    @Json(name = "Oraszam")  val classCount: Int
)