package com.commonsware.todo_3

import androidx.lifecycle.ViewModel

class SingleModelMotor(
    private val repo: ToDoRepository,
    private val modelId: String
) : ViewModel() {
    fun getModel() = repo.find(modelId)
}