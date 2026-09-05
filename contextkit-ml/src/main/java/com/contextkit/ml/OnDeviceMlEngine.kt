package com.contextkit.ml

import com.contextkit.core.ContextAnalyzerEngine
import com.contextkit.core.EngineClassification

class OnDeviceMlEngine(
    private val runner: ModelRunner,
    private val minimumConfidence: Float = 0.65f
) : ContextAnalyzerEngine {

    override suspend fun classify(text: String): EngineClassification {
        val prediction = runner.predict(ModelInput(text))
        val confidence = prediction.confidence.coerceIn(0f, 1f)

        return EngineClassification(
            category = prediction.category,
            confidence = confidence,
            engine = if (confidence >= minimumConfidence) {
                "ml:${prediction.modelId}:${prediction.modelVersion}"
            } else {
                "ml-low-confidence"
            }
        )
    }
}
