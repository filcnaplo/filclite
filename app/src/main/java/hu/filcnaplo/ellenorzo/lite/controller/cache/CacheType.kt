package hu.filcnaplo.ellenorzo.lite.controller.cache

import android.net.NetworkCapabilities

enum class CacheType(val validities: Map<CacheHandler.NetworkQuality, Long>) {
    EvaluationList(mapOf(CacheHandler.NetworkQuality.Metered to 120L,
        CacheHandler.NetworkQuality.Wired to 30L,
        CacheHandler.NetworkQuality.Normal to 60L))
}