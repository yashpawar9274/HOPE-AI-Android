package com.yashpawar.hopeai.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class HopeStore(context: Context) {
    private val prefs = context.getSharedPreferences("hope_v2_store", Context.MODE_PRIVATE)

    var language: HopeLanguage
        get() = runCatching { HopeLanguage.valueOf(prefs.getString("language", null) ?: "AUTO") }.getOrDefault(HopeLanguage.AUTO)
        set(value) { prefs.edit().putString("language", value.name).apply() }

    var displayName: String
        get() = prefs.getString("display_name", "Yash") ?: "Yash"
        set(value) { prefs.edit().putString("display_name", value.trim()).apply() }

    var session: AuthSession?
        get() {
            val token = prefs.getString("access_token", null) ?: return null
            return AuthSession(token, prefs.getString("refresh_token", "") ?: "", prefs.getString("email", "") ?: "")
        }
        set(value) {
            if (value == null) prefs.edit().remove("access_token").remove("refresh_token").remove("email").apply()
            else prefs.edit().putString("access_token", value.accessToken).putString("refresh_token", value.refreshToken).putString("email", value.email).apply()
        }

    fun loadTasks(): List<TaskItem> = parseArray("tasks") { json ->
        TaskItem(json.getString("id"), json.getString("title"), json.optBoolean("completed"))
    }

    fun saveTasks(items: List<TaskItem>) = saveArray("tasks", items.map {
        JSONObject().put("id", it.id).put("title", it.title).put("completed", it.completed)
    })

    fun loadMemories(): List<MemoryItem> = parseArray("memories") { json ->
        MemoryItem(json.getString("id"), json.getString("content"), json.optLong("createdAt"))
    }

    fun saveMemories(items: List<MemoryItem>) = saveArray("memories", items.map {
        JSONObject().put("id", it.id).put("content", it.content).put("createdAt", it.createdAt)
    })

    private fun <T> parseArray(key: String, parse: (JSONObject) -> T): List<T> = runCatching {
        val array = JSONArray(prefs.getString(key, "[]"))
        List(array.length()) { parse(array.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    private fun saveArray(key: String, values: List<JSONObject>) {
        prefs.edit().putString(key, JSONArray(values).toString()).apply()
    }
}

