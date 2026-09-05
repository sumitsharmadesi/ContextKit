# ContextKit V3 Architecture

## 1. Design principle

ContextKit is a platform, not the Smart Clipboard app.

The app is only a consumer of a stable SDK API.

```text
+-----------------------------+
|       Consumer Apps         |
| Clipboard | Notes | Expense|
+--------------+--------------+
               |
               v
+-----------------------------+
|       ContextKit API        |
| analyze / analyzeAsync      |
+--------------+--------------+
               |
       +-------+-------+
       |               |
       v               v
+-------------+   +-------------+
| Rule Engine |   | ML Engine   |
| deterministic|  | on-device   |
+------+------+   +------+------+
       |                 |
       +--------+--------+
                v
       +-------------------+
       | Unified Result    |
       | category/entities|
       | confidence/actions|
       +-------------------+
```

## 2. Module boundaries

### core

Owns the contract.

It must not depend on a specific ML runtime. This keeps the base SDK small and lets developers use ContextKit without shipping an ML model.

### ml

Owns model execution.

It can depend on a specific runtime later. The important abstraction is:

```kotlin
interface ModelRunner {
    suspend fun predict(input: ModelInput): ModelPrediction
}
```

A future implementation might be:

```text
LiteRtModelRunner
        |
        v
tokenizer -> tensor -> inference -> label mapping
```

The rest of the SDK remains unchanged.

### storage

Owns persistence only.

This module should remain optional. Apps that do not want history should not need a database dependency.

## 3. Inference pipeline

```text
Input text
   |
   v
Normalize
   |
   +-------------------+
   |                   |
   v                   v
Rule hints          ML inference
   |                   |
   +---------+---------+
             |
             v
      Confidence policy
             |
      +------+------+
      |             |
   high conf      low conf
      |             |
      v             v
   ML result     rule result
      |             |
      +------+------+
             |
             v
     Entity extraction
             |
             v
      Action suggestion
             |
             v
       ContextResult
```

The confidence policy prevents a weak model prediction from becoming an authoritative result.

## 4. Why rules remain

Rules are useful for:
- URLs
- emails
- phone numbers
- currency
- obvious tracking IDs
- deterministic entities

ML is useful for:
- intent classification
- ambiguous text
- personalized classification
- future semantic similarity

Using both gives better reliability than forcing every task through ML.

## 5. Threading

Public asynchronous analysis should eventually execute inference off the main thread.

The V3 API exposes a `suspend` method so applications can use their own coroutine scope.

For production:
- CPU inference -> `Dispatchers.Default`
- disk -> `Dispatchers.IO`
- UI -> app's lifecycle scope

## 6. Model lifecycle

A future `ModelManager` should own:

```text
Model ID
Version
Input schema
Output schema
SHA-256/checksum
File size
Minimum SDK
Quantization type
Supported locales
```

Model loading should happen once and reuse an interpreter/session.

## 7. Versioning

Keep public API compatibility strict.

Suggested:

```text
1.0.0
MAJOR = breaking API
MINOR = new backwards-compatible capability
PATCH = bug fix
```

Do not expose model implementation classes from `contextkit-core`.

## 8. Publishing

Each library can be published as an AAR/Maven artifact:

```text
com.contextkit:contextkit-core
com.contextkit:contextkit-ml
com.contextkit:contextkit-storage
```

Consumers that only need classification can depend on core. Consumers that want the model runtime can opt into ML.

Android's official library documentation confirms that Android library modules build AARs and can be consumed from Maven repositories. See:
https://developer.android.com/studio/projects/android-library

## 9. Security/privacy

Core should have:
- no network calls
- no telemetry
- no automatic persistence

If telemetry is ever introduced, it belongs in a separate optional module and must be explicit.

## 10. Long-term evolution

```text
V3
Rules + ML abstraction
        |
V3.1
Real LiteRT classifier
        |
V4
Embeddings + semantic search
        |
V5
Personalized local model
        |
V6
Local context graph / agent
```

The key is that the public API does not need to change for each stage.
