package com.example.todoapplication.data.repository

import androidx.room.Delete
import androidx.room.Query
import com.example.todoapplication.data.model.Todo
import kotlinx.coroutines.flow.Flow


interface TodoRepository {
    suspend fun addTodos(todo: Todo)
    fun getTodos(): Flow<List<Todo>>
    suspend fun deleteTodo(todo: Todo)
    suspend fun updateTodos(todo: Todo)
    fun getTodoByDateAdded(): Flow<List<Todo>>
    fun getTodoByIsDone(): Flow<List<Todo>>
    fun getTodosByPriority(): Flow<List<Todo>>
    fun markTodoDone(id:Int)
}