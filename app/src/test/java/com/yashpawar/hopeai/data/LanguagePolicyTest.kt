package com.yashpawar.hopeai.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguagePolicyTest {
    @Test fun detectsHindi() = assertEquals(HopeLanguage.HINDI, LanguagePolicy.detect("मुझे मदद चाहिए"))
    @Test fun detectsHinglish() = assertEquals(HopeLanguage.HINGLISH, LanguagePolicy.detect("mujhe task add karna hai"))
    @Test fun detectsEnglish() = assertEquals(HopeLanguage.ENGLISH, LanguagePolicy.detect("Please add a task"))
    @Test fun detectsEmoji() {
        assertTrue(LanguagePolicy.containsEmoji("Hello 😀"))
        assertFalse(LanguagePolicy.containsEmoji("Hello HOPE"))
    }
}

