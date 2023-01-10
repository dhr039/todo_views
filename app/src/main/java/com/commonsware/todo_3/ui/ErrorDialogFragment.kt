package com.commonsware.todo_3.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.commonsware.todo_3.R

class ErrorDialogFragment : DialogFragment() {
    companion object {
        const val KEY_RETRY = "retryRequested"
    }

    private val args: ErrorDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(args.title)
            .setMessage(args.message)
            .setPositiveButton(R.string.retry) { _, _ -> onRetryRequest() }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
    }

    /*
        • Get our NavController, for access to the Navigation component APIs
        • Find the BackStackEntry corresponding to whatever it was that displayed
        this dialog
        • Get a SavedStateHandle for that BackStackEntry, where SavedStateHandle
        is a bit like a HashMap (key-value store of data)
        • Set the KEY_RETRY value in that state to be the ErrorScenario that was
        passed into us via the navigation arguments
        With this in place, when the user clicks the “Retry” button, we update this
        SavedStateHandle with the ErrorScenario that triggered the dialog. (p. 568)
    * */
    private fun onRetryRequest() {
        findNavController()
            .previousBackStackEntry?.savedStateHandle?.set(KEY_RETRY, args.scenario)
    }
}

enum class ErrorScenario { Import, None }
