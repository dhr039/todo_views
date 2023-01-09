package com.commonsware.todo_3.repo

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * The recommended way is to treat the Web service data model as
 * being distinct from the app’s data model, with conversions between them as needed.
 * This is similar to how we have our Room entities defined separately from our
 * models, so any changes in Room do not affect our core app logic. We
 * are going to funnel our server responses into the database, so we will be converting Web service
 * responses into entities that we can attempt to insert into the database. (p. 527)
 * */
@JsonClass(generateAdapter = true)
data class ToDoServerItem(
    val description: String,
    val id: String,
    val completed: Boolean,
    val notes: String,
    @Json(name = "created_on") val createdOn: Instant
) {
    fun toEntity(): ToDoEntity {
        return ToDoEntity(
            id = id,
            description = description,
            isCompleted = completed,
            notes = notes,
            createdOn = createdOn
        )
    }
}

private val FORMATTER = DateTimeFormatter.ISO_INSTANT

class MoshiInstantAdapter {
    @ToJson
    fun toJson(date: Instant) = FORMATTER.format(date)

    @FromJson
    fun fromJson(dateString: String): Instant =
        FORMATTER.parse(dateString, Instant::from)
}
