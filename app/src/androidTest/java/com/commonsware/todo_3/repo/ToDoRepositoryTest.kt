package com.commonsware.todo_3.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * TODO: Investigate why createdOn is not the same(truncated) and test are failing.
 * (page 395)
java.lang.AssertionError:
Expected: <ToDoModel(description=test model, id=08c19a38-8512-47a2-8ac9-a45e9d060695, isCompleted=false, notes=, createdOn=2022-12-28T11:42:25.811533Z)>
but: was  <ToDoModel(description=test model, id=08c19a38-8512-47a2-8ac9-a45e9d060695, isCompleted=false, notes=, createdOn=2022-12-28T11:42:25.811Z)>
 * */
@RunWith(AndroidJUnit4::class)
class ToDoRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = ToDoDatabase.newTestInstance(context)

    @Test
    fun canAddItems() = runBlockingTest {
        val underTest = ToDoRepository(db.toDoStore(), this)
        val results = mutableListOf<List<ToDoModel>>()

        val itemsJob = launch {
            underTest.items().collect { results.add(it) }
        }

        assertThat(results.size, equalTo(1))
        assertThat(results[0], empty())

        val testModel = ToDoModel("test model")

        underTest.save(testModel)

        assertThat(results.size, equalTo(2))
        assertThat(results[1], contains(testModel))
        assertThat(underTest.find(testModel.id).first(), equalTo(testModel))

        itemsJob.cancel()
    }

    @Test
    fun canModifyItems() = runBlockingTest {
        val underTest = ToDoRepository(db.toDoStore(), this)
        val testModel = ToDoModel("test model")
        val replacement = testModel.copy(notes = "This is the replacement")
        val results = mutableListOf<List<ToDoModel>>()

        val itemsJob = launch {
            underTest.items().collect { results.add(it) }
        }

        assertThat(results[0], empty())

        underTest.save(testModel)

        assertThat(results[1], contains(testModel))

        underTest.save(replacement)

        assertThat(results[2], contains(replacement))

        itemsJob.cancel()
    }

    @Test
    fun canRemoveItems() = runBlockingTest {
        val underTest = ToDoRepository(db.toDoStore(), this)
        val testModel = ToDoModel("test model")
        val results = mutableListOf<List<ToDoModel>>()

        val itemsJob = launch {
            underTest.items().collect { results.add(it) }
        }

        assertThat(results[0], empty())

        underTest.save(testModel)

        assertThat(results[1], contains(testModel))

        underTest.delete(testModel)

        assertThat(results[2], empty())

        itemsJob.cancel()
    }
}

