package hu.filcnaplo.ellenorzo.lite.kreta.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate

class KretaDateAdapter {
    @FromJson
    fun fromJson(dateString: String): KretaDate {
        return KretaDate().fromString(dateString)
    }
    @ToJson
    fun toJson(kretaDate: KretaDate): String {
        return kretaDate.toString()
    }
}

