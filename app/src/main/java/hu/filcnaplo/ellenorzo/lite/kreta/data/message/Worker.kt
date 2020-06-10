package hu.filcnaplo.ellenorzo.lite.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Worker(
    @Json(name = "kretaAzonosito") val kretaId: Int,
    @Json(name = "isAlairo") val isSigner: Boolean?,
    @Json(name = "nev") val name: String,
    @Json(name = "titulus") val role: String?,
    var type: hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Type?
) {
    fun toReceiver(): Receiver {
        return Receiver(null, kretaId, null, null, name, null, type)
    }
}

