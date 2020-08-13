package hu.filcnaplo.ellenorzo.lite.kreta

open class KretaError (
    val reason: ErrorReason
) {
    class VolleyError(reason: ErrorReason, val volley: com.android.volley.VolleyError): KretaError(reason)
    class ParseError(reason: ErrorReason): KretaError(reason)
    class EmptyError(reason: ErrorReason): KretaError(reason)
}

enum class ErrorReason {
    Empty,
    Unknown,
    NoConnectionError,
    Invalid,
    VolleyError
}

