package com.contextkit.core

interface ContextAnalyzerEngine {
    suspend fun classify(text: String): EngineClassification
}

data class EngineClassification(
    val category: ContextCategory,
    val confidence: Float,
    val engine: String
)
