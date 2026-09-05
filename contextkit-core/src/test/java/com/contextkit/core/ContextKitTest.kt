package com.contextkit.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextKitTest {
    @Test
    fun detectsMoney() {
        val result = ContextKit.analyze("₹1,250 paid to Swiggy")
        assertEquals(ContextCategory.MONEY, result.category)
        assertTrue(result.entities.any { it.type == "MONEY" })
    }

    @Test
    fun detectsReminder() {
        val result = ContextKit.analyze("Meeting with Rahul tomorrow")
        assertEquals(ContextCategory.REMINDER, result.category)
    }

    @Test
    fun detectsUrl() {
        val result = ContextKit.analyze("https://example.com")
        assertEquals(ContextCategory.URL, result.category)
    }
}
