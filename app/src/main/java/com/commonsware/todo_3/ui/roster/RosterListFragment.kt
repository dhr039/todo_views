package com.commonsware.todo_3.ui.roster

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.commonsware.todo_3.R
import com.commonsware.todo_3.databinding.TodoRosterBinding
import com.commonsware.todo_3.repo.ToDoModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RosterListFragment : Fragment() {
    private val motor: RosterMotor by viewModel()
    private var binding: TodoRosterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            RosterAdapter(
                layoutInflater,
                onCheckboxToggle = { motor.save(it.copy(isCompleted = !it.isCompleted)) },
                onRowClick = ::display
            )

        binding?.items?.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(
                    activity, DividerItemDecoration.VERTICAL
                )
            )
        }

        //TODO: replace launchWhenStarted
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.states.collect { state ->
                adapter.submitList(state.items)

                binding?.apply {
                    when {
                        state.items.isEmpty() -> {
                            empty.visibility = View.VISIBLE
                            empty.setText(R.string.msg_empty)
                        }
                        else -> empty.visibility = View.GONE
                    }
                }
            }
        }

        binding?.empty?.visibility = View.GONE
    }

    private fun add() {
        findNavController().navigate(RosterListFragmentDirections.createModel(null))
    }

    private fun display(model: ToDoModel) {
        findNavController().navigate(RosterListFragmentDirections.displayModel(model.id))
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}