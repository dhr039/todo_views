package com.commonsware.todo_3.ui

import com.commonsware.todo_3.repo.ToDoModel
import com.commonsware.todo_3.repo.ToDoRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class SingleModelMotorTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(paused = true)

    private val testModel = ToDoModel("this is a test")

    private val repo: ToDoRepository = mock {
        on { find(testModel.id) } doReturn flowOf(testModel)
    }

    private lateinit var underTest: SingleModelMotor

    @Before
    fun setUp() {
        underTest = SingleModelMotor(repo, testModel.id)
    }

    @Test
    fun `initial state`() {
        mainDispatcherRule.dispatcher.runCurrent()

        runBlocking {
            val item = underTest.states.first().item

            assertEquals(testModel, item)
        }
    }

    @Test
    fun `actions pass through to repo`() {
        val replacement = testModel.copy("whatevs")

        underTest.save(replacement)
        mainDispatcherRule.dispatcher.runCurrent()

        runBlocking { verify(repo).save(replacement) }

        underTest.delete(replacement)
        mainDispatcherRule.dispatcher.runCurrent()

        runBlocking { verify(repo).delete(replacement) }
    }
}

