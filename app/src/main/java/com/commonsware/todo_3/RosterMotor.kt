package com.commonsware.todo_3

import androidx.lifecycle.ViewModel

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    fun getItems() = repo.items
    fun save(model: ToDoModel) {
        repo.save(model)
    }
}