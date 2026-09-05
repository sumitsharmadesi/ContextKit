package com.contextkit.core

internal class RuleEngine : ContextAnalyzerEngine {
    override suspend fun classify(text: String): EngineClassification {
        val t = text.trim()
        val c = when {
            Regex("^https?://\\S+$", RegexOption.IGNORE_CASE).matches(t) ->
                ContextCategory.URL to .99f
            Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE).matches(t) ->
                ContextCategory.EMAIL to .99f
            Regex("^(?:\\+?91[- ]?)?[6-9]\\d{9}$").matches(t.replace(" ", "")) ->
                ContextCategory.PHONE_NUMBER to .97f
            Regex(".*(?:₹|rs\\.?|inr|\\$|usd)\\s?\\d+(?:[.,]\\d{1,2})?.*", RegexOption.IGNORE_CASE).matches(t) ->
                ContextCategory.MONEY to .92f
            Regex(".*(?:tomorrow|today|meeting|remind me|at \\d{1,2}(?::\\d{2})?\\s?(?:am|pm)?).*", RegexOption.IGNORE_CASE).matches(t) ->
                ContextCategory.REMINDER to .86f
            Regex(".*(?:tracking|tracking id|shipped|delivery|delivered|order #).*", RegexOption.IGNORE_CASE).matches(t) ->
                ContextCategory.DELIVERY to .86f
            else -> ContextCategory.UNKNOWN to .20f
        }
        return EngineClassification(c.first, c.second, "rules")
    }
}
