package com.contextkit.core

enum class ContextCategory {
    PHONE_NUMBER, EMAIL, URL, MONEY, REMINDER, DELIVERY, ADDRESS, DATE_TIME, UNKNOWN
}

enum class ActionType {
    CALL, EMAIL, OPEN_URL, ADD_EXPENSE, CREATE_REMINDER, TRACK_DELIVERY, COPY
}

data class ContextEntity(
    val type: String,
    val value: String,
    val start: Int,
    val end: Int
)

data class SuggestedAction(
    val type: ActionType,
    val label: String
)

data class ContextResult(
    val originalText: String,
    val category: ContextCategory,
    val confidence: Float,
    val entities: List<ContextEntity>,
    val suggestedActions: List<SuggestedAction>,
    val engine: String
)
