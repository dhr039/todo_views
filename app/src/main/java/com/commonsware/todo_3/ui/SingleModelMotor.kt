package com.commonsware.todo_3.ui

import androidx.lifecycle.ViewModel
import com.commonsware.todo_3.repo.ToDoModel
import com.commonsware.todo_3.repo.ToDoRepository

class SingleModelMotor(
    private val repo: ToDoRepository,
    private val modelId: String?
) : ViewModel() {

    fun getModel() = repo.find(modelId)

    fun save(model: ToDoModel) {
        repo.save(model)
    }

    fun delete(model: ToDoModel) {
        repo.delete(model)
    }
}