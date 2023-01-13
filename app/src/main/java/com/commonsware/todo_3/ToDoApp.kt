package com.commonsware.todo_3

import android.app.Application
import android.text.format.DateUtils
import androidx.work.*
import com.commonsware.todo_3.repo.*
import com.commonsware.todo_3.report.RosterReport
import com.commonsware.todo_3.ui.SingleModelMotor
import com.commonsware.todo_3.ui.roster.RosterMotor
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Instant
import java.util.concurrent.TimeUnit

private const val TAG_IMPORT_WORK = "doPeriodicImport"

class ToDoApp : Application(), KoinComponent {
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

        /**
         * appScope is set up to live for as long as our process does, so any coroutines executed from within it
         * will get to run to completion, even if viewModelScope gets canceled. (p. 357)
         * */
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

        scheduleWork()
    }

    private fun scheduleWork() {
        val prefs: PrefsRepository by inject()
        val appScope: CoroutineScope by inject(named("appScope"))
        val workManager = WorkManager.getInstance(this)

        appScope.launch {
            prefs.observeImportChanges().collect {
                if (it) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request =
                        PeriodicWorkRequestBuilder<ImportWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag(TAG_IMPORT_WORK)
                            .build()

                    workManager.enqueueUniquePeriodicWork(
                        TAG_IMPORT_WORK,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                    )
                } else {
                    workManager.cancelAllWorkByTag(TAG_IMPORT_WORK)
                }
            }
        }
    }

}