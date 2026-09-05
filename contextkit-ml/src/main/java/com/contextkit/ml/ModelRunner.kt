package com.contextkit.ml

import com.contextkit.core.ContextCategory

interface ModelRunner {
    suspend fun predict(input: ModelInput): ModelPrediction
}

data class ModelInput(
    val text: String,
    val locale: String = "en"
)

data class ModelPrediction(
    val category: ContextCategory,
    val confidence: Float,
    val modelId: String,
    val modelVersion: String
)
