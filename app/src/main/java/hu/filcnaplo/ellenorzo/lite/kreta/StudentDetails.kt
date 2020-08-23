package hu.filcnaplo.ellenorzo.lite.kreta

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Tutelary

@JsonClass(generateAdapter = true)
class StudentDetails (
    @Json(name = "Uid")  val uid: String,
    @Json(name = "AnyjaNeve")  val mother: String?,
    @Json(name = "Cimek")  val addresses: List<String>,
    @Json(name = "Gondviselok")  val tutelaries: List<Tutelary>,
    @Json(name = "IntezmenyAzonosito")  val instituteIdentifier: String,
    @Json(name = "IntezmenyNev")  val instituteName: String,
    @Json(name = "Nev")  val name: String,
    @Json(name = "SzuletesiDatum")  val birthDate: KretaDate,
    @Json(name = "SzuletesiHely")  val birthPlace: String,
    @Json(name = "SzuletesiNev")  val birthName: String,
    @Json(name = "TanevUid")  val yearUid: String
    ) {
    override fun toString(): String {
        return name
    }
    fun toDetailedString(): String {
        var addressString = ""
        for (address in addresses) {
            addressString += "\n    $address"
        }
        var tutelariesString = ""
        for (tutelary in tutelaries) {
            tutelariesString += "\n    ${tutelary.name} (${tutelary.uid})"
            if (tutelary.email != null)
                tutelariesString += "\n         ${tutelary.email}"
            if (tutelary.phoneNumber != null)
            tutelariesString += "\n         ${tutelary.phoneNumber}"
        }
        return  "Uid:\n    $uid" +
                "\nMother's name:\n    $mother" +
                "\nAddress list:$addressString" +
                "\nTutelaries:$tutelariesString" +
                "\nInstitute:\n    $instituteName ($instituteIdentifier)" +
                "\nBirth date:\n    ${birthDate.toFormattedString(KretaDate.KretaDateFormat.DATE)}" +
                "\nBirth place:\n    $birthPlace" +
                "\nBirth name:\n    $birthName" +
                "\nYear uid:\n    $yearUid"
    }
}