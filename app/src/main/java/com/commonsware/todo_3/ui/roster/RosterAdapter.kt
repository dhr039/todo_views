package com.commonsware.todo_3.ui.roster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.commonsware.todo_3.databinding.TodoRowBinding
import com.commonsware.todo_3.repo.ToDoModel

class RosterAdapter(
    private val inflater: LayoutInflater,
    private val onCheckboxToggle: (ToDoModel) -> Unit,
    private val onRowClick: (ToDoModel) -> Unit
) :
    ListAdapter<ToDoModel, RosterRowHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RosterRowHolder(
            TodoRowBinding.inflate(inflater, parent, false),
            onCheckboxToggle,
            onRowClick
        )

    override fun onBindViewHolder(holder: RosterRowHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object DiffCallback : DiffUtil.ItemCallback<ToDoModel>() {
    override fun areItemsTheSame(oldItem: ToDoModel, newItem: ToDoModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ToDoModel, newItem: ToDoModel) =
        oldItem.isCompleted == newItem.isCompleted &&
                oldItem.description == newItem.description
}
