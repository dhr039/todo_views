package com.commonsware.todo_3

class ToDoRepository {
    var items = emptyList<ToDoModel>()

    fun save(model: ToDoModel) {
        items = if (items.any { it.id == model.id }) {
            items.map { if (it.id == model.id) model else it }
        } else {
            items + model
        }
    }

    fun delete(model: ToDoModel) {
        items = items.filter { it.id != model.id }
    }

    fun find(modelId: String?) = items.find { it.id == modelId }

}
