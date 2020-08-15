package hu.filcnaplo.ellenorzo.lite.controller.cache

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.NetworkCapabilities
import android.util.Log
import androidx.room.Room
import hu.filcnaplo.ellenorzo.lite.kreta.KretaRequests
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CacheHandler(val ctx: Context) {
    private val db = Room.databaseBuilder(ctx, CacheDatabase::class.java, "cache")
        .build()

    private val lastRequest = mutableMapOf<Map<CacheType, String>, Long>()
    private val isCachedValue = mutableMapOf(
        CacheType.EvaluationList to false
    )
    
    var networkCapabilities: NetworkCapabilities? = null
    
    private fun now(): Long {
        return System.currentTimeMillis() / 1000
    }

    enum class NetworkQuality {
        Metered,
        Wired,
        Normal
    }
    private fun getNetworkQuality(networkCapabilities: NetworkCapabilities?): NetworkQuality {
        return if (networkCapabilities != null) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) { //TODO: When somehow?
                NetworkQuality.Wired
            } else if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
                NetworkQuality.Normal
            } else {
                NetworkQuality.Metered
            }
        } else {
            NetworkQuality.Normal
        }
    }
    
    fun requestedExternalSource(type: CacheType, unique: String) {
        lastRequest[mapOf(type to unique)] = now()
    }

    fun shouldUseCache(type: CacheType, unique: String): Boolean {
        val quality = getNetworkQuality(networkCapabilities)
        val last = lastRequest[mapOf(type to unique)]
        return if (last != null) {
            type.validities[quality] ?: type.validities[NetworkQuality.Normal] ?: 0 > (now() - last) //TODO: MAYBE NOT TURNING FALSE CHECK IT OUT
        } else {
            false
        }
    }
    
    fun isCachedReturn(cacheType: CacheType): Boolean {
        val cached = isCachedValue.getOrDefault(cacheType, false)
        isCachedValue[cacheType] = false
        return cached
    }
    
    fun cacheEvaluationList(evals: List<Evaluation>) {
        GlobalScope.launch {
            db.evalDao().insertList(evals)
        }
    }
    fun getEvaluationListCache(listener: KretaRequests.OnEvaluationListResult) {
        GlobalScope.launch {
            val evals = db.evalDao().getAll()
            launch(Dispatchers.Main) {
                listener.onEvaluationListSuccess(evals)
            }
            isCachedValue[CacheType.EvaluationList] = true
        }
    }
}