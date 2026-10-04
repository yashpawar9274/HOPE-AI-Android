package com.yashpawar.hopeai.data

import java.util.UUID

enum class HopeLanguage(val label: String) {
    AUTO("Auto"), ENGLISH("English"), HINDI("Hindi"), HINGLISH("Hinglish")
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false,
)

data class MemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
)

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>
    data class Failure(val message: String) : NetworkResult<Nothing>
}

object LanguagePolicy {
    private val devanagari = Regex("[\\u0900-\\u097F]")
    private val hinglishMarkers = setOf("hai", "hain", "kya", "mujhe", "mera", "karna", "batao", "yaad")

    fun detect(text: String): HopeLanguage {
        if (devanagari.containsMatchIn(text)) return HopeLanguage.HINDI
        val words = text.lowercase().split(Regex("[^a-z]+"))
        return if (words.any(hinglishMarkers::contains)) HopeLanguage.HINGLISH else HopeLanguage.ENGLISH
    }

    fun containsEmoji(text: String): Boolean = text.codePoints().anyMatch {
        it in 0x1F300..0x1FAFF || it in 0x2600..0x27BF
    }
}

