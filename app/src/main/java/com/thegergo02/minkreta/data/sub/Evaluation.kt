package com.thegergo02.minkreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.KretaDate

@JsonClass(generateAdapter = true)
class Evaluation(
    @Json(name = "EvaluationId")  val id: Int?,
    @Json(name = "Form") val form: String?,
    @Json(name = "FormName") val formName: String?,
    @Json(name = "Type") val type: String?,
    @Json(name = "TypeName") val typeName: String?,
    @Json(name = "Subject") val subject: String?,
    @Json(name = "SubjectCategory") val subjectCategory: String?,
    @Json(name = "SubjectCategoryName") val subjectCategoryName: String?,
    @Json(name = "Theme") val theme: String?,
    @Json(name = "IsAtlagbaBeleszamit") val countsIntoAverage: Boolean?,
    @Json(name = "Mode") val mode: String?,
    @Json(name = "Weight") val weight: String?,
    @Json(name = "Value") val value: String?,
    @Json(name = "NumberValue") val numberValue: Int?,
    @Json(name = "SeenByTutelaryUTC") val seenByTutelaryUtc: String?,
    @Json(name = "Teacher") val teacher: String?,
    @Json(name = "Date") val date: KretaDate?,
    @Json(name = "CreatingTime") val creatingTime: KretaDate?,
    @Json(name = "Jelleg") val nature: Nature?,
    @Json(name = "JellegNev") val natureName: String?,
    @Json(name = "ErtekFajta") val valueType: Nature?,
    @Json(name = "OsztalyCsoportUid") val classGroupUid: String?
) {
    override fun toString(): String {
        if (form == "Diligence" || form == "Deportment") {
            return  "$natureName ($teacher) \n" +
                    "${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
        }
        return  "$subject ($teacher) \n" +
                "$value ($weight) \n" +
                "$theme \n" +
                "${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
    fun toDetailedString(): String {
        return  "$subject ($teacher)\n" +
                "$value ($weight) \n" +
                "$typeName \n" +
                "$formName \n" +
                "$theme \n" +
                "${creatingTime?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)}"
    }
}