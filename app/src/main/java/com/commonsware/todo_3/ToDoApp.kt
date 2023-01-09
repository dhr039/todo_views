package com.commonsware.todo_3

import android.app.Application
import android.text.format.DateUtils
import com.commonsware.todo_3.repo.PrefsRepository
import com.commonsware.todo_3.repo.ToDoDatabase
import com.commonsware.todo_3.repo.ToDoRemoteDataSource
import com.commonsware.todo_3.repo.ToDoRepository
import com.commonsware.todo_3.report.RosterReport
import com.commonsware.todo_3.ui.SingleModelMotor
import com.commonsware.todo_3.ui.roster.RosterMotor
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Instant

class ToDoApp : Application() {
    private val koinModule = module {
        single {
            ToDoRepository(
                get<ToDoDatabase>().toDoStore(),
                get(named("appScope")),
                get()
            )
        }
        viewModel { RosterMotor(get(), get(), androidApplication(), get(named("appScope")), get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
        single { ToDoDatabase.newInstance(androidContext()) }
        single(named("appScope")) { CoroutineScope(SupervisorJob()) }
        single {
            Handlebars().apply {
                registerHelper("dateFormat", Helper<Instant> { value, _ ->
                    DateUtils.getRelativeDateTimeString(
                        androidContext(),
                        value.toEpochMilli(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS, 0
                    )
                })
            }
        }
        single { RosterReport(androidContext(), get(), get(named("appScope"))) }
        single { OkHttpClient.Builder().build() }
        single { ToDoRemoteDataSource(get()) }
        single { PrefsRepository(androidContext()) }
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