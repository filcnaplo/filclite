package hu.filcnaplo.ellenorzo.lite.kreta.data.message

import com.squareup.moshi.Json
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Type

class Receiver(
    @Json(name = "azonosito") val id: Int?,
    @Json(name = "kretaAzonosito") val kretaId: Int,
    @Json(name = "kod") val code: String?,
    @Json(name = "rovidNev") val shortName: String?,
    @Json(name = "nev") val name: String,
    @Json(name = "leiras") val description: String?,
    @Json(name = "tipus") val type: Type?
) {
    override fun toString(): String {
        return "        {\n" +
                "            \"azonosito\": $id,\n" +
                "            \"kretaAzonosito\": $kretaId,\n" +
                "            \"nev\": \"$name\",\n" +
                "            \"tipus\": $type}"
    }
}
