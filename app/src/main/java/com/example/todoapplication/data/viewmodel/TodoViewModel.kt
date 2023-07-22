package com.example.todoapplication.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.data.model.Todo
import com.example.todoapplication.data.repository.TodoRepositoryImpl
import com.example.todoapplication.data.room.TodoEvent
import com.example.todoapplication.data.util.SortType
import com.example.todoapplication.data.util.TodoState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class TodoViewModel (private val repositoryImpl: TodoRepositoryImpl) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.GET_ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _todos = _sortType.flatMapLatest {
        when (it) {
            SortType.IS_DONE -> repositoryImpl.getTodoByIsDone()
            SortType.DATE_ADDED -> repositoryImpl.getTodoByDateAdded()
            SortType.GET_ALL -> repositoryImpl.getTodos()
            SortType.PRIORITY -> repositoryImpl.getTodosByPriority()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(TodoState())

     val state = combine(_state, _sortType, _todos) { state, sortTypes, todos ->
        state.copy(
            todoList = todos,
            sortType = sortTypes,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodoState())


    fun onEvent(event: TodoEvent) {
        when (event) {
            TodoEvent.SaveTodo -> {
                Log.d("VIEW_MODEL","Hi from view Model")
                val todos = state.value.todo
                val priority = state.value.priority
                val isDone = state.value.isDone
                val sD = state.value.shortDescription
                val color = state.value.color
                val date = Calendar.getInstance().time.time
                if (todos.isEmpty() || priority == 0 || sD.isEmpty()) {
                    return
                }
                val todo = Todo(
                    color,
                    todos,
                    priority,
                    isDone,
                    sD,
                    date,
                )
                viewModelScope.launch {
                    Log.d("VIEW_MODEL","Hi from view Model coroutine")
                    repositoryImpl.addTodos(todo)
                }
                _state.update {
                    it.copy(
                        todo = "",
                        priority = 0,
                        isDone = false,
                        shortDescription = "",
                        color = 0L,
                        addedDate = 0L
                    )
                }
            }
            is TodoEvent.deleteTodo ->{
                viewModelScope.launch {
                    repositoryImpl.deleteTodo(event.todo)
                }
            }
            is TodoEvent.setIsDone -> {
                _state.update {
                    it.copy(
                        isDone = event.isDone
                    )
                }
            }
            is TodoEvent.setPriority -> {
                _state.update {
                    it.copy(
                        priority = event.p
                    )
                }
            }
            is TodoEvent.setShortDescription -> {
                _state.update {
                    it.copy(
                        shortDescription = event.sD
                    )
                }
            }
            is TodoEvent.setTodo -> {
                _state.update {
                    it.copy(
                        todo = event.todoText
                    )
                }
            }
            is TodoEvent.UpdateTodos -> {
                viewModelScope.launch {
                    Log.d("VIEW_MODEL","Hi from view Update")
                    repositoryImpl.updateTodos(event.todo)
                }
                _state.update {
                    it.copy(
                        todo = "",
                        priority = 0,
                        isDone = false,
                        shortDescription = "",
                        color = 0L,
                        addedDate = 0L
                    )
                }
            }

            is TodoEvent.setAddedDate -> {
                _state.update {
                    it.copy(
                        addedDate = event.date
                    )
                }
            }

            is TodoEvent.SortTodo -> {
                _sortType.value = event.sortType
            }

            is TodoEvent.setColor -> {
                _state.update {
                    it.copy(
                        color = event.col
                    )
                }
            }

            is TodoEvent.markTodoDone -> {
                viewModelScope.launch {
                    repositoryImpl.markTodoDone(id = event.id)
                }
            }
        }
    }

}