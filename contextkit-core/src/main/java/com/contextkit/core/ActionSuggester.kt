package com.contextkit.core

internal object ActionSuggester {
    fun suggest(category: ContextCategory): List<SuggestedAction> = when (category) {
        ContextCategory.PHONE_NUMBER -> listOf(SuggestedAction(ActionType.CALL, "Call"))
        ContextCategory.EMAIL -> listOf(SuggestedAction(ActionType.EMAIL, "Email"))
        ContextCategory.URL -> listOf(SuggestedAction(ActionType.OPEN_URL, "Open URL"))
        ContextCategory.MONEY -> listOf(SuggestedAction(ActionType.ADD_EXPENSE, "Add expense"))
        ContextCategory.REMINDER, ContextCategory.DATE_TIME ->
            listOf(SuggestedAction(ActionType.CREATE_REMINDER, "Create reminder"))
        ContextCategory.DELIVERY ->
            listOf(SuggestedAction(ActionType.TRACK_DELIVERY, "Track delivery"))
        else -> listOf(SuggestedAction(ActionType.COPY, "Copy"))
    }
}
