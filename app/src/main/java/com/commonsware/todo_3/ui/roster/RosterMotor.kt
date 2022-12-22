package com.commonsware.todo_3.ui.roster

import androidx.lifecycle.ViewModel
import com.commonsware.todo_3.repo.ToDoModel
import com.commonsware.todo_3.repo.ToDoRepository

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    fun getItems() = repo.items
    fun save(model: ToDoModel) {
        repo.save(model)
    }
}