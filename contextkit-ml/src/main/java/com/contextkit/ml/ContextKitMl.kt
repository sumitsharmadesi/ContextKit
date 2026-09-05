package com.contextkit.ml

import com.contextkit.core.ContextKit

object ContextKitMl {
    fun install(
        runner: ModelRunner,
        minimumConfidence: Float = 0.65f
    ) {
        ContextKit.installEngine(
            OnDeviceMlEngine(runner, minimumConfidence)
        )
    }
}
