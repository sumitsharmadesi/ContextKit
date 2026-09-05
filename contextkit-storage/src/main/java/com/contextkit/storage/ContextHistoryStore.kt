package com.contextkit.storage

import com.contextkit.core.ContextResult

interface ContextHistoryStore {
    suspend fun save(result: ContextResult)
    suspend fun recent(limit: Int = 50): List<ContextResult>
    suspend fun clear()
}

/**
 * In-memory reference implementation.
 * Replace with Room/DataStore in a production application.
 */
class InMemoryContextHistoryStore : ContextHistoryStore {
    private val items = ArrayDeque<ContextResult>()

    override suspend fun save(result: ContextResult) {
        items.addFirst(result)
        while (items.size > 100) items.removeLast()
    }

    override suspend fun recent(limit: Int): List<ContextResult> =
        items.take(limit.coerceAtLeast(0))

    override suspend fun clear() = items.clear()
}
