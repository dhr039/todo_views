package com.commonsware.todo_3

import android.app.Application
import com.commonsware.todo_3.repo.ToDoDatabase
import com.commonsware.todo_3.repo.ToDoRepository
import com.commonsware.todo_3.ui.SingleModelMotor
import com.commonsware.todo_3.ui.roster.RosterMotor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class ToDoApp : Application() {
    private val koinModule = module {
        single {
            ToDoRepository(
                get<ToDoDatabase>().toDoStore(),
                get(named("appScope"))
            )
        }
        viewModel { RosterMotor(get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
        single { ToDoDatabase.newInstance(androidContext()) }
        single(named("appScope")) { CoroutineScope(SupervisorJob()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            /**
             * Technically, we could bypass all of this and have our single() in koinModule use 'this'
             * instead of androidContext(). The downside of that approach is that if we wanted a
             * different Context in testing, we would be unable to provide it. Basically, Koin allows
             * us to inject the top-level Context in addition to injecting our own classes. (p.340)
             * */
            androidContext(this@ToDoApp)

            modules(koinModule)
        }
    }
}