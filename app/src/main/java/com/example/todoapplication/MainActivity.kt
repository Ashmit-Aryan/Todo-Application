package com.example.todoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.todoapplication.data.repository.TodoRepositoryImpl
import com.example.todoapplication.data.room.TodoDatabase
import com.example.todoapplication.data.util.TodoState
import com.example.todoapplication.data.viewmodel.TodoViewModel
import com.example.todoapplication.screen.AddTodoScreen
import com.example.todoapplication.screen.TodoListScreen
import com.example.todoapplication.screen.UpdateTodoScreen
import com.example.todoapplication.ui.theme.TodoApplicationTheme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<TodoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(TodoRepositoryImpl(db.dao)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodoApplicationTheme {
                val navController = rememberNavController()
                val state by viewModel.state.collectAsState()
                NavHost(navController = navController, startDestination = "todo_home_screen") {
                    composable(
                        "todo_home_screen",
                    ) {
                        TodoListScreen(
                            state = state,
                            onEvent = viewModel::onEvent,
                            navControllers = navController
                        )
                    }
                    composable("todo_add_screen") {
                        AddTodoScreen(
                            state = state,
                            navControllerFinal = navController,
                            onEvent = viewModel::onEvent
                        )
                    }
                    composable("todo_update_screen/{id}", arguments = listOf(
                        navArgument(
                           "id"
                        ){
                            type = NavType.IntType
                        }
                    ) ) {
                        val id = remember {
                            it.arguments?.getInt("id")
                        }
                        val tobeUpdatedState = state.todoList.find { it.id == id }
                        UpdateTodoScreen(state = tobeUpdatedState!!, onEvent = viewModel::onEvent, navControllerFinal = navController)
                    }
                }
            }
        }
    }
}

