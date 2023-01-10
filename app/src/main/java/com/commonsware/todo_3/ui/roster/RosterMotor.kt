package com.commonsware.todo_3.ui.roster

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonsware.todo_3.BuildConfig
import com.commonsware.todo_3.repo.FilterMode
import com.commonsware.todo_3.repo.PrefsRepository
import com.commonsware.todo_3.repo.ToDoModel
import com.commonsware.todo_3.repo.ToDoRepository
import com.commonsware.todo_3.report.RosterReport
import com.commonsware.todo_3.ui.ErrorScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val isLoaded: Boolean = false,
    val filterMode: FilterMode = FilterMode.ALL
)

sealed class Nav {
    data class ViewReport(val doc: Uri) : Nav()
    data class ShareReport(val doc: Uri) : Nav()
}

class RosterMotor(
    private val repo: ToDoRepository,
    private val report: RosterReport,
    private val context: Application,
    private val appScope: CoroutineScope,
    private val prefs: PrefsRepository
) : ViewModel() {
//    val states = repo.items().map { RosterViewState(it, true) }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, RosterViewState())

    /**
     * we have one or more Flow objects with our items, and we want to funnel them all
     * into a single StateFlow of RosterViewState objects (p.430-431)
     * */
    private val _states = MutableStateFlow(RosterViewState()) // state
    val states = _states.asStateFlow()
    private var job: Job? = null
    private val _navEvents = MutableSharedFlow<Nav>() // event
    val navEvents = _navEvents.asSharedFlow()
    private val _errorEvents = MutableSharedFlow<ErrorScenario>()
    val errorEvents = _errorEvents.asSharedFlow()


    init {
        load(FilterMode.ALL)
    }

    fun load(filterMode: FilterMode) {
        job?.cancel()

        job = viewModelScope.launch {
            repo.items(filterMode).collect {
                _states.emit(RosterViewState(it, true, filterMode))
            }
        }
    }

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }

    fun saveReport(doc: Uri) {
        viewModelScope.launch {
            report.generate(_states.value.items, doc)
            _navEvents.emit(Nav.ViewReport(doc))
        }
    }

    fun shareReport() {
        viewModelScope.launch {
            saveForSharing()
        }
    }

    private suspend fun saveForSharing() {
        withContext(Dispatchers.IO + appScope.coroutineContext) {
            val shared = File(context.cacheDir, "shared").also { it.mkdirs() }
            val reportFile = File(shared, "report.html")
            val doc = FileProvider.getUriForFile(context, AUTHORITY, reportFile)

            _states.value.let { report.generate(it.items, doc) }
            _navEvents.emit(Nav.ShareReport(doc))
        }
    }

    fun importItems() {
        viewModelScope.launch {
            try {
                repo.importItems(prefs.loadWebServiceUrl())
            } catch (ex: Exception) {
                Log.e("ToDo", "Exception importing items", ex)
                _errorEvents.emit(ErrorScenario.Import)
            }
        }
    }

}
