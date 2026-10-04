package com.yashpawar.hopeai.data

import com.yashpawar.hopeai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class HopeBackendClient {
    val supabaseConfigured: Boolean get() = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()
    val aiConfigured: Boolean get() = BuildConfig.BACKEND_URL.isNotBlank()

    suspend fun signIn(email: String, password: String): NetworkResult<AuthSession> {
        if (!supabaseConfigured) return NetworkResult.Failure("Supabase is not configured in this build.")
        return request(
            url = "${BuildConfig.SUPABASE_URL.trimEnd('/')}/auth/v1/token?grant_type=password",
            body = JSONObject().put("email", email).put("password", password),
            headers = mapOf("apikey" to BuildConfig.SUPABASE_ANON_KEY),
        ) { json ->
            AuthSession(json.getString("access_token"), json.optString("refresh_token"), email)
        }
    }

    suspend fun signUp(email: String, password: String): NetworkResult<String> {
        if (!supabaseConfigured) return NetworkResult.Failure("Supabase is not configured in this build.")
        return request(
            url = "${BuildConfig.SUPABASE_URL.trimEnd('/')}/auth/v1/signup",
            body = JSONObject().put("email", email).put("password", password),
            headers = mapOf("apikey" to BuildConfig.SUPABASE_ANON_KEY),
        ) { "Account created. Check your email if verification is enabled." }
    }

    suspend fun chat(text: String, language: HopeLanguage, accessToken: String?): NetworkResult<String> {
        if (!aiConfigured) return NetworkResult.Failure("AI backend is not configured. Add HOPE_BACKEND_URL to local.properties or CI secrets.")
        return request(
            url = "${BuildConfig.BACKEND_URL.trimEnd('/')}/chat",
            body = JSONObject().put("message", text).put("language", language.name.lowercase()),
            headers = accessToken?.let { mapOf("Authorization" to "Bearer $it") }.orEmpty(),
        ) { it.optString("reply").ifBlank { error("Empty AI response") } }
    }

    suspend fun registerFcmToken(token: String, accessToken: String?): NetworkResult<Unit> {
        if (!aiConfigured || accessToken.isNullOrBlank()) return NetworkResult.Failure("Backend session unavailable")
        return request(
            url = "${BuildConfig.BACKEND_URL.trimEnd('/')}/devices/register",
            body = JSONObject().put("token", token).put("platform", "android"),
            headers = mapOf("Authorization" to "Bearer $accessToken"),
        ) { Unit }
    }

    private suspend fun <T> request(
        url: String,
        body: JSONObject,
        headers: Map<String, String>,
        parse: (JSONObject) -> T,
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 45_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                headers.forEach { (name, value) -> setRequestProperty(name, value) }
            }
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                val message = runCatching { JSONObject(text).optString("message") }.getOrNull().orEmpty()
                return@runCatching NetworkResult.Failure(message.ifBlank { "Request failed with code $code" })
            }
            NetworkResult.Success(parse(if (text.isBlank()) JSONObject() else JSONObject(text)))
        }.getOrElse { NetworkResult.Failure(it.message ?: "Network request failed") }
    }
}
