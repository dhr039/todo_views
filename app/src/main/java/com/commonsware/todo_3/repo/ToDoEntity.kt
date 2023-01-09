package com.commonsware.todo_3.repo

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

/**
 * This class has the same properties as ToDoModel.
 * Mostly, that is for realism: there is no guarantee that your entities will have a 1:1 relationship with models.
 * Room puts restrictions on how entities can be constructed, particularly when it comes to relationships with
 * other entities. Things that you might do in model objects (e.g., a category object holding a collection of
 * item objects) wind up having to be implemented significantly differently using Room entities. Those details
 * will get hidden by your repositories. A repository exists in part to convert specialized forms of your data
 * (Room entities, Web service responses, etc.) into the model objects that your UI is set up to use. (page 334)
 * */
@Entity(tableName = "todos", indices = [Index(value = ["id"])])
data class ToDoEntity(
    val description: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val notes: String = "",
    val createdOn: Instant = Instant.now(), //sqlite doesn't have a native date/time column type and room will give a compile error. Need a TypeConverter.
    val isCompleted: Boolean = false
) {

    /*from Model to Entity*/
    constructor(model: ToDoModel) : this(
        id = model.id,
        description = model.description,
        isCompleted = model.isCompleted,
        notes = model.notes,
        createdOn = model.createdOn
    )

    /*from Entity to Model*/
    fun toModel(): ToDoModel {
        return ToDoModel(
            id = id,
            description = description,
            isCompleted = isCompleted,
            notes = notes,
            createdOn = createdOn
        )
    }

    @Dao
    interface Store {
        @Query("SELECT * FROM todos ORDER BY description")
        fun all(): Flow<List<ToDoEntity>>

        @Query("SELECT * FROM todos WHERE isCompleted = :isCompleted ORDER BY description")
        fun filtered(isCompleted: Boolean): Flow<List<ToDoEntity>>

        @Query("SELECT * FROM todos WHERE id = :modelId")
        fun find(modelId: String?): Flow<ToDoEntity?>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(vararg entities: ToDoEntity)

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun importItems(entities: List<ToDoEntity>)

        @Delete
        suspend fun delete(vararg entities: ToDoEntity)
    }
}