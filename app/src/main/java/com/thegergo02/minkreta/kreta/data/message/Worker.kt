package com.thegergo02.minkreta.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.data.message.Receiver
import java.lang.reflect.Type

@JsonClass(generateAdapter = true)
class Worker(
    @Json(name = "kretaAzonosito") val kretaId: Int,
    @Json(name = "isAlairo") val isSigner: Boolean?,
    @Json(name = "nev") val name: String,
    @Json(name = "titulus") val role: String?,
    var type: com.thegergo02.minkreta.kreta.data.sub.Type?
) {
    fun toReceiver(): Receiver {
        return Receiver(null, kretaId, null, null, name, null, type)
    }
}

