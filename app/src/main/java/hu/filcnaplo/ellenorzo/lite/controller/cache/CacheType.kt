package hu.filcnaplo.ellenorzo.lite.controller.cache

private val default = mapOf(CacheHandler.NetworkQuality.Metered to 120L,
    CacheHandler.NetworkQuality.Wired to 30L,
    CacheHandler.NetworkQuality.Normal to 60L)

enum class CacheType(val validities: Map<CacheHandler.NetworkQuality, Long>) {
    EvaluationList(default),
    MessageList(default),
    NoteList(default)
}