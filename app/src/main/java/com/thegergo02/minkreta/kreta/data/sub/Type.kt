package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Type(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "kod") val code: String,
    @Json(name = "rovidNev") val shortName: String,
    @Json(name = "nev") val name: String,
    @Json(name = "leiras") val description: String
) {
    override fun toString(): String {
        return "{\n" +
                "                \"azonosito\": $id,\n" +
                "                \"kod\": \"$code\",\n" +
                "                \"rovidNev\": \"$shortName\",\n" +
                "                \"nev\": \"$name\",\n" +
                "                \"leiras\": \"$description\"\n" +
                "            }"
    }
}