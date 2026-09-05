package com.contextkit.core

object ContextKit {
    private var engine: ContextAnalyzerEngine = RuleEngine()

    @Synchronized
    fun installEngine(newEngine: ContextAnalyzerEngine) {
        engine = newEngine
    }

    suspend fun analyzeAsync(text: String): ContextResult {
        require(text.isNotBlank()) { "text must not be blank" }

        val classification = engine.classify(text)
        return ContextResult(
            originalText = text,
            category = classification.category,
            confidence = classification.confidence.coerceIn(0f, 1f),
            entities = EntityExtractor.extract(text),
            suggestedActions = ActionSuggester.suggest(classification.category),
            engine = classification.engine
        )
    }

    /**
     * Synchronous convenience API for simple non-production use/tests.
     * Prefer analyzeAsync from Android UI code.
     */
    fun analyze(text: String): ContextResult {
        require(text.isNotBlank()) { "text must not be blank" }
        val classification = RuleEngineBlocking.classify(text)
        return ContextResult(
            originalText = text,
            category = classification.category,
            confidence = classification.confidence,
            entities = EntityExtractor.extract(text),
            suggestedActions = ActionSuggester.suggest(classification.category),
            engine = classification.engine
        )
    }
}

private object RuleEngineBlocking {
    fun classify(text: String): EngineClassification {
        val t = text.trim()
        val c = when {
            Regex("^https?://\\S+$", RegexOption.IGNORE_CASE).matches(t) -> ContextCategory.URL to .99f
            Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE).matches(t) -> ContextCategory.EMAIL to .99f
            Regex("^(?:\\+?91[- ]?)?[6-9]\\d{9}$").matches(t.replace(" ", "")) -> ContextCategory.PHONE_NUMBER to .97f
            Regex(".*(?:₹|rs\\.?|inr|\\$|usd)\\s?\\d+(?:[.,]\\d{1,2})?.*", RegexOption.IGNORE_CASE).matches(t) -> ContextCategory.MONEY to .92f
            Regex(".*(?:tomorrow|today|meeting|remind me).*", RegexOption.IGNORE_CASE).matches(t) -> ContextCategory.REMINDER to .86f
            Regex(".*(?:tracking|tracking id|shipped|delivery|delivered|order #).*", RegexOption.IGNORE_CASE).matches(t) -> ContextCategory.DELIVERY to .86f
            else -> ContextCategory.UNKNOWN to .20f
        }
        return EngineClassification(c.first, c.second, "rules")
    }
}
