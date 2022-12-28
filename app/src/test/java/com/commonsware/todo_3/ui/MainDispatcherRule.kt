package com.commonsware.todo_3.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// inspired by https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471
/**
 * We need to do something in our app to teach the coroutines system what to use
when we reference Dispatchers.Main in our code. The coroutines testing library
that we just added contains a TestCoroutineDispatcher that we can use, but we
need to tell the coroutines system to use a TestCoroutineDispatcher for
Dispatchers.Main. (page 385)
 * */
class MainDispatcherRule(paused: Boolean) : TestWatcher() {
    val dispatcher =
        TestCoroutineDispatcher().apply { if (paused) pauseDispatcher() }

    override fun starting(description: Description?) {
        super.starting(description)

        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)

        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }
}
