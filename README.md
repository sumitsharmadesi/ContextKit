# ContextKit

ContextKit is a reusable, privacy-first Android text intelligence SDK.
The first reference product is Smart Clipboard.

## Goals

- Stable, small public API
- Separate core, ML runtime, and storage modules
- On-device ML extension point
- Confidence-aware fallback
- Async API
- Model metadata/versioning
- Local analysis history
- Maven/AAR-ready library structure
- App-independent SDK

Android libraries compile to AARs and can be consumed by app modules or published to a Maven repository. See the official Android library guidance:
https://developer.android.com/studio/projects/android-library

## Modules

### contextkit-core
No ML runtime dependency. Contains:
- public data models
- ContextKit facade
- analyzer engine interface
- rule-based fallback
- entity extraction
- action suggestion

### contextkit-ml
Optional ML layer:
- ModelRunner interface
- model metadata
- OnDeviceMlEngine
- confidence threshold/fallback policy
- placeholder for a LiteRT implementation

The starter intentionally does not include a binary model. Add your model later under the ML module's model/assets area and implement `ModelRunner`.

### contextkit-storage
Optional local persistence abstraction for analyzed clipboard/context items.

### app
Smart Clipboard reference application.

## Public API

```kotlin
val result = ContextKit.analyze("Meeting with Rahul tomorrow at 5 PM")
```

Async:

```kotlin
val result = ContextKit.analyzeAsync(
    "₹1,250 paid to Swiggy"
)
```

Install a custom engine:

```kotlin
ContextKit.installEngine(myEngine)
```

## Recommended ML path

1. Train a small intent classifier with labels such as MONEY, REMINDER, DELIVERY, CONTACT, URL, UNKNOWN.
2. Export/convert it to a mobile-friendly model.
3. Quantize it where accuracy permits.
4. Implement `ModelRunner` in `contextkit-ml`.
5. Keep the public `ContextKit` API unchanged.
6. Add model version/checksum and benchmark tests.
7. Use rules as a safety/fallback layer when model confidence is low.

## Production packaging

The SDK is structured as Android library modules so each library can produce an AAR. When the API stabilizes, publish artifacts to a Maven repository. Android's publishing guidance covers AAR metadata, consumer ProGuard rules, namespaces and Maven publication.

## Privacy model

Default design:
- text stays on device
- no network dependency in core
- no analytics in core
- app controls whether clipboard data is persisted
- ML module is optional

## V1 / V2 ideas

- LiteRT model runner
- embeddings module
- semantic search
- configurable entity extractors
- model download/update manager
- developer diagnostics
- benchmark suite
- Java interoperability tests
- Maven Central/GitHub Packages publication
