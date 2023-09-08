package com.astute_vision.shop_navigator.gate

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

open class Gate {
    private val httpClient: OkHttpClient
    private val cookieJar: CookieJar
    private var cookie: MutableList<Cookie> = mutableListOf()

    //    private var user: String
//        get() {
//            return userHolder[0]
//        }
//        set(value) {
//            userHolder[0] = value
//        }
//    private var pass: String
//        get() {
//            return passHolder[0]
//        }
//        set(value) {10.132.31.63:8000\register
//            passHolder[0] = value
//        }
    private var baseUrl: String = "http://192.168.138.8:8000/"
    private var tokenHolder = arrayOf("")
    private var userHolder = arrayOf("")
    private var passHolder = arrayOf("")

//    fun setComponents(user: String = "", pass: String = "") {
//        this.pass = pass
//        this.user = user
//    }


    val format = Json {
        encodeDefaults = false
        prettyPrint = true//Удобно печатает в несколько строчек
        ignoreUnknownKeys = true// Неизвестные значение
        coerceInputValues = true// Позволяет кодировать в параметрах null
        explicitNulls = true// Позволяет декодировать в параметрах null
    }


    init {
        cookieJar = object : CookieJar {


            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                if (cookie.isNotEmpty()) {
                    return cookie
                }
                return mutableListOf()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookie = cookies as MutableList<Cookie>
            }
        }


        val b = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(15000, TimeUnit.MILLISECONDS)


//        b.authenticator(
//            TokenAuthenticator(
//                format,
//                baseUrl + "users/login",
//                userHolder,
//                passHolder,
//                tokenHolder
//            )
//        )


        httpClient = b.build()

    }


    inline fun <reified T> makePostRequest(
        url: String,
        body: T,
        query: Map<String, String> = mapOf()
    ): String {
        val json = format.encodeToString(value = body)
        return makePostRequestImpl(json, url, query)
    }


    fun makePostRequestImpl(
        json: String,
        url: String,
        query: Map<String, String>
    ): String {
        val contentType = "application/json".toMediaTypeOrNull()


        val b = if (json.isEmpty()) {
            RequestBody.create(null, ByteArray(0), 0, 0)
        } else {
            json.toRequestBody(contentType)
        }

        var urlWithParams: String? = null

        if (query.isNotEmpty()) {
            val urlBuilder = "$baseUrl$url".toHttpUrlOrNull()?.newBuilder()

            query.forEach { (k, v) ->
                urlBuilder?.addQueryParameter(k, v)
            }

            urlWithParams = urlBuilder?.build().toString()
        }

        val request = Request.Builder()
            .post(b)
            .url(urlWithParams ?: "$baseUrl$url")
            .build()

        val r = httpClient.newCall(request).execute()

        if (r.isSuccessful) {
            return r.body!!.string()
        } else {
            throw RuntimeException("Ошибка запроса ${r.message}")
        }
    }

    fun makeGetRequest(
        url: String,
        query: Map<String, String> = mapOf()
    ): String {

        var urlWithParams: String? = null

        if (query.isNotEmpty()) {
            val urlBuilder = "$baseUrl$url".toHttpUrlOrNull()?.newBuilder()

            query.forEach { (k, v) ->
                urlBuilder?.addQueryParameter(k, v)
            }

            urlWithParams = urlBuilder?.build().toString()
        }


        val request = Request.Builder()
            .get()
            .url(urlWithParams ?: "$baseUrl$url")
            .build()

        val r = httpClient.newCall(request).execute()

        if (r.isSuccessful) {
            return r.body!!.string()
        } else {
            throw RuntimeException("Ошибка запроса ${r.message}")
        }
    }

    inline fun <reified T> makePatchRequest(
        url: String,
        body: T,
        query: Map<String, String> = mapOf()
    ): String {
        val json = format.encodeToString(value = body)
        return makePatchRequestImpl(json, url, query)
    }

    fun makePatchRequestImpl(
        json: String,
        url: String,
        query: Map<String, String>
    ): String {
        val contentType = "application/json".toMediaTypeOrNull()
        val b = json.toRequestBody(contentType)

        var urlWithParams: String? = null

        if (query.isNotEmpty()) {
            val urlBuilder = "$baseUrl$url".toHttpUrlOrNull()?.newBuilder()

            query.forEach { (k, v) ->
                urlBuilder?.addQueryParameter(k, v)
            }

            urlWithParams = urlBuilder?.build().toString()
        }

        val request = Request.Builder()
            .patch(b)
            .url(urlWithParams ?: "$baseUrl$url")
            .build()

        val r = httpClient.newCall(request).execute()

        if (r.isSuccessful) {
            return r.body!!.string()
        } else {
            throw RuntimeException("Ошибка запроса ${r.message}")
        }
    }

    private inline fun <reified T> makePutRequest(
        url: String,
        body: T,
        query: Map<String, String> = mapOf()
    ): String {
        val json = format.encodeToString(value = body)
        return makePutRequestImpl(json, url, query)
    }

    private fun makePutRequestImpl(
        json: String,
        url: String,
        query: Map<String, String>
    ): String {
        val contentType = "application/json".toMediaTypeOrNull()
        val b = json.toRequestBody(contentType)

        var urlWithParams: String? = null

        if (query.isNotEmpty()) {
            val urlBuilder = "$baseUrl$url".toHttpUrlOrNull()?.newBuilder()

            query.forEach { (k, v) ->
                urlBuilder?.addQueryParameter(k, v)
            }

            urlWithParams = urlBuilder?.build().toString()
        }

        val request = Request.Builder()
            .put(b)
            .url(urlWithParams ?: "$baseUrl$url")
            .build()

        val r = httpClient.newCall(request).execute()

        if (r.isSuccessful) {
            return r.body!!.string()
        } else {
            throw RuntimeException("Ошибка запроса ${r.message}")
        }
    }
}