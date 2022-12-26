package com.commonsware.todo_3.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DB_NAME = "stuff.db"

@TypeConverters(TypeTransmogrifier::class)
@Database(entities = [ToDoEntity::class], version = 1)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun toDoStore(): ToDoEntity.Store

    companion object {
        fun newInstance(context: Context) =
            Room.databaseBuilder(context, ToDoDatabase::class.java, DB_NAME).build()

        /*For tests it is very convenient to have an in-memory database*/
        fun newTestInstance(context: Context) =
            Room.inMemoryDatabaseBuilder(context, ToDoDatabase::class.java).build()
    }
}