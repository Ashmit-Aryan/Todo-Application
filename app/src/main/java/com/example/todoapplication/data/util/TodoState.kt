package com.example.todoapplication.data.util

import com.example.todoapplication.data.model.Todo
import java.util.Date

data class TodoState(
    val todoList: List<Todo> = emptyList(),
    val color:Long = 0L,
    val todo: String="",
    val priority: Int = 0,
    val isDone: Boolean=false,
    val shortDescription: String="",
    val addedDate: Long = 0L,
    val sortType: SortType = SortType.GET_ALL
)