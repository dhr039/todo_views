package com.commonsware.todo_3.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ToDoRepository(
    private val store: ToDoEntity.Store,
    private val appScope: CoroutineScope
) {
    fun items(): Flow<List<ToDoModel>> =
        store.all().map { all -> all.map { it.toModel() } }
//            .onStart { delay(5000) }

    fun find(id: String?): Flow<ToDoModel?> = store.find(id).map { it?.toModel() }

    suspend fun save(model: ToDoModel) {
        withContext(appScope.coroutineContext) {
            store.save(ToDoEntity(model))
        }
    }

    suspend fun delete(model: ToDoModel) {
        withContext(appScope.coroutineContext) {
            store.delete(ToDoEntity(model))
        }
    }
}

