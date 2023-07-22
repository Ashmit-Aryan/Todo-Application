package com.example.todoapplication.data.room

import com.example.todoapplication.data.model.Todo
import com.example.todoapplication.data.util.SortType
import java.text.SimpleDateFormat
import java.util.Date

sealed interface TodoEvent {
    object SaveTodo : TodoEvent
    data class markTodoDone(val id:Int):TodoEvent
    data class UpdateTodos(val todo: Todo):TodoEvent
    data class deleteTodo(val todo: Todo):TodoEvent
    data class setTodo(val todoText: String) : TodoEvent
    data class setPriority(val p: Int) : TodoEvent
    data class setIsDone(val isDone: Boolean) : TodoEvent
    data class setAddedDate(val date: Long) : TodoEvent
    data class setColor(val col:Long):TodoEvent
    data class setShortDescription(val sD: String) : TodoEvent
    data class SortTodo(val sortType: SortType): TodoEvent
}