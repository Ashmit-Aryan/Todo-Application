package com.example.todoapplication.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.todoapplication.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo( todo: Todo)
    
    @Update
    suspend fun updateTodo(todo: Todo)
    
    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM todo")
    fun getTodos(): Flow<List<Todo>>

    @Query("update todo set isDone = :done where id = :id")
    fun markIsDone(done:Boolean,id:Int)

    @Query("SELECT * FROM todo ORDER BY date DESC")
    fun getTodoByDateAdded(): Flow<List<Todo>>

    @Query("select * from todo where isDone like 'true'")
    fun getTodoByIsDone(): Flow<List<Todo>>

    @Query("select * from todo order by priority DESC")
    fun getTodosByPriority(): Flow<List<Todo>>


}