package com.thegergo02.minkreta.kreta

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

open class NetworkRequest(
    val method: Int,
    val url: String,
    val headers: Map<NetworkHelper.Header, String>? = null,
    val contentType: String? = null,
    val body: String? = null
)

class NetworkJsonObjectRequest(method: Int,
                               url: String,
                               val successListener: Response.Listener<JSONObject>,
                               val errorListener: Response.ErrorListener,
                               headers: Map<NetworkHelper.Header, String>? = null,
                               contentType: String? = null,
                               body: String? = null): NetworkRequest(method, url, headers, contentType, body)
class NetworkJsonArrayRequest(method: Int,
                               url: String,
                               val successListener: Response.Listener<JSONArray>,
                               val errorListener: Response.ErrorListener,
                               headers: Map<NetworkHelper.Header, String>? = null,
                               contentType: String? = null,
                               body: String? = null): NetworkRequest(method, url, headers, contentType, body)
class NetworkStringRequest(method: Int,
                              url: String,
                              val successListener: Response.Listener<String>,
                              val errorListener: Response.ErrorListener,
                              headers: Map<NetworkHelper.Header, String>? = null,
                              contentType: String? = null,
                              body: String? = null): NetworkRequest(method, url, headers, contentType, body)

class NetworkHelper(ctx: Context) {
    val queue = Volley.newRequestQueue(ctx) //TODO: MAKE PRIVATE!!!!!!!

    enum class Header(val key: String) {
        ApiKey("apiKey"),
        Auth("Authorization"),
        UserAgent("User-Agent"),
        Accept("Accept"),
        ManagementLocalization("X-Uzenet-Lokalizacio")
    }

    private fun generateHeaders(headers: Map<Header, String>?): MutableMap<String ,String> {
        val realHeaders = mutableMapOf<String, String>()
        if (headers != null) {
            for (header in headers) {
                realHeaders[header.key.key] = header.value
            }
        }
        return realHeaders
    }

    fun requestJsonObject(request: NetworkJsonObjectRequest) {
        val query = object : JsonObjectRequest(request.method, request.url, null, request.successListener, request.errorListener) {
            override fun getHeaders(): MutableMap<String, String> = generateHeaders(request.headers)
            override fun getBodyContentType(): String = request.contentType ?: ""
            override fun getBody(): ByteArray = request.body?.toByteArray() ?: "".toByteArray()
        }
        queue.add(query)
    }

    fun requestJsonArray(request: NetworkJsonArrayRequest) {
        val query = object : JsonArrayRequest(request.method, request.url, null, request.successListener, request.errorListener) {
            override fun getHeaders(): MutableMap<String, String> = generateHeaders(request.headers)
            override fun getBodyContentType(): String = request.contentType ?: ""
            override fun getBody(): ByteArray = request.body?.toByteArray() ?: "".toByteArray()
        }
        queue.add(query)
    }

    fun requestString(request: NetworkStringRequest) {
        val query = object : StringRequest(request.method, request.url, request.successListener, request.errorListener) {
            override fun getHeaders(): MutableMap<String, String> = generateHeaders(request.headers)
            override fun getBodyContentType(): String = request.contentType ?: ""
            override fun getBody(): ByteArray = request.body?.toByteArray() ?: "".toByteArray()
        }
        queue.add(query)
    }
}