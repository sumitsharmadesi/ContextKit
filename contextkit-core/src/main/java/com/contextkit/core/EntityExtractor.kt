package com.contextkit.core

internal object EntityExtractor {
    fun extract(text: String): List<ContextEntity> {
        val result = mutableListOf<ContextEntity>()

        fun add(type: String, regex: Regex) {
            regex.findAll(text).forEach {
                result += ContextEntity(type, it.value, it.range.first, it.range.last + 1)
            }
        }

        add("EMAIL", Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE))
        add("URL", Regex("https?://\\S+", RegexOption.IGNORE_CASE))
        add("PHONE", Regex("(?:\\+?91[- ]?)?[6-9]\\d{9}"))
        add("MONEY", Regex("(?:₹|Rs\\.?|INR|\\$|USD)\\s?\\d+(?:[.,]\\d{1,2})?", RegexOption.IGNORE_CASE))
        add("TIME", Regex("\\b\\d{1,2}(?::\\d{2})?\\s?(?:AM|PM)\\b", RegexOption.IGNORE_CASE))
        add("DATE_HINT", Regex("\\b(?:today|tomorrow|tonight|monday|tuesday|wednesday|thursday|friday|saturday|sunday)\\b", RegexOption.IGNORE_CASE))
        return result.distinctBy { "${it.type}:${it.start}:${it.end}" }
    }
}
