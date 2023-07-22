package com.example.todoapplication.data.repository

import com.example.todoapplication.data.model.Todo
import com.example.todoapplication.data.room.TodoDao
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl (private val dao: TodoDao) : TodoRepository {
    override suspend fun addTodos(todo: Todo) {
        return dao.insertTodo(todo)
    }

    override fun getTodos(): Flow<List<Todo>> {
        return dao.getTodos()
    }

    override suspend fun deleteTodo(todo: Todo) {
        return dao.deleteTodo(todo)
    }

    override suspend fun updateTodos(todo: Todo) {
        return dao.updateTodo(todo)
    }

    override fun getTodoByDateAdded(): Flow<List<Todo>> {
        return dao.getTodoByDateAdded()
    }

    override fun getTodoByIsDone(): Flow<List<Todo>> {
        return dao.getTodoByIsDone()
    }

    override fun getTodosByPriority(): Flow<List<Todo>> {
        return dao.getTodosByPriority()
    }

    override fun markTodoDone(id: Int) {
        return dao.markIsDone(true,id)
    }
}