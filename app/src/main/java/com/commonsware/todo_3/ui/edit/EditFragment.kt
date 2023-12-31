package com.commonsware.todo_3.ui.edit

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.commonsware.todo_3.R
import com.commonsware.todo_3.databinding.TodoEditBinding
import com.commonsware.todo_3.repo.ToDoModel
import com.commonsware.todo_3.ui.SingleModelMotor
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditFragment : Fragment() {
    private var binding: TodoEditBinding? = null
    private val args: EditFragmentArgs by navArgs()
    private val motor: SingleModelMotor by viewModel { parametersOf(args.modelId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_edit, menu)
        menu.findItem(R.id.delete).isVisible = args.modelId != null

        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                save()
                return true
            }
            R.id.delete -> {
                delete()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        binding?.apply {
            val model = motor.states.value.item
            val edited = model?.copy(
                description = desc.text.toString(),
                isCompleted = isCompleted.isChecked,
                notes = notes.text.toString()
            ) ?: ToDoModel(
                description = desc.text.toString(),
                isCompleted = isCompleted.isChecked,
                notes = notes.text.toString()
            )

            edited.let { motor.save(it) }
        }
        navToDisplay()
    }

    private fun delete() {
        val model = motor.states.value.item
        model?.let { motor.delete(it) }
        navToList()
    }

    private fun navToList() {
        hideKeyboard()
        findNavController().popBackStack(R.id.rosterListFragment, false)
    }

    private fun navToDisplay() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoEditBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.states.collect { state ->
                //after a config change Android will automatically populate widgets, we just need to not screw it up (p.360):
                if (savedInstanceState == null) {
                    state.item?.let {
                        binding?.apply {
                            isCompleted.isChecked = it.isCompleted
                            desc.setText(it.description)
                            notes.setText(it.notes)
                        }
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}