package com.commonsware.todo_3.repo

import androidx.room.TypeConverter
import java.time.Instant

/**
 * sqlite doesn't have a native date/time column type and room will give a compile error. Need a TypeConverter.
 * (see ToDoEntity.kt)
 * */
object TypeTransmogrifier {
    @TypeConverter
    fun fromInstant(date: Instant?): Long? = date?.toEpochMilli()

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? = millisSinceEpoch?.let {
        Instant.ofEpochMilli(it)
    }
}
