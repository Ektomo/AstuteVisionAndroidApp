package com.astute_vision.shop_navigator.gate

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class TokenAuthenticator(
    private val json: Json,
    private val url: String,
    private val username: Array<String>,
    private val password: Array<String>,
    private val token: Array<String>
) :
    Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {

        val originalRequest = response.request
        if (originalRequest.header("Authorization") != null) return null

        if (token[0].isEmpty()) {

            val obj = URL(url)
            val connection: HttpURLConnection = obj.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible)")
//        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.doInput = true

            val dataString = getDataString(mapOf(Pair("username", username[0]), Pair("password", password[0])))

//            val jsonInputString = """{"username": "${username[0]}", "password": "${password[0]}"}"""
            connection.outputStream.use { os ->
                val input = dataString!!.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            var inputLine: String?
            val r = StringBuffer()


            while (bufferedReader.readLine().also { inputLine = it } != null) {
                r.append(inputLine)
            }
            bufferedReader.close()

            val j = json.decodeFromString<TokenResponse>(r.toString())

            token[0] = j.access_token
        }

        return originalRequest
            .newBuilder()
            .header(
                "Authorization",
                "Bearer ${token[0]}"
            )
            .build()

    }
}



private fun getDataString(params: Map<String, String>): String? {
    val result = StringBuilder()
    var first = true
    for ((key, value) in params) {
        if (first) first = false else result.append("&")
        result.append(URLEncoder.encode(key, "UTF-8"))
        result.append("=")
        result.append(URLEncoder.encode(value, "UTF-8"))
    }
    return result.toString()
}

@kotlinx.serialization.Serializable
data class TokenResponse(
    val access_token: String
)