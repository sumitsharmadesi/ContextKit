package com.contextkit.ml

data class ModelMetadata(
    val modelId: String,
    val version: String,
    val sha256: String,
    val inputSchema: String,
    val outputSchema: String,
    val quantization: String? = null
)
