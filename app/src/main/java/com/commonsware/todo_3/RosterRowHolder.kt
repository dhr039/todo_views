package com.commonsware.todo_3

import androidx.recyclerview.widget.RecyclerView
import com.commonsware.todo_3.databinding.TodoRowBinding

class RosterRowHolder(
    private val binding: TodoRowBinding,
    val onCheckBoxToggle: (ToDoModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: ToDoModel) {
        binding.apply {
            isCompleted.isChecked = model.isCompleted
            isCompleted.setOnCheckedChangeListener { _, _ -> onCheckBoxToggle(model) }
            desc.text = model.description
        }
    }
}