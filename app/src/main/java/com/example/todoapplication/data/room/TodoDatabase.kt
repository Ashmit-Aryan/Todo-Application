package com.example.todoapplication.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapplication.data.model.Todo

@Database(
    entities = [Todo::class],
    version = 1,

)
abstract class TodoDatabase:RoomDatabase() {
    abstract val dao: TodoDao

}