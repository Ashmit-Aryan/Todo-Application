package com.example.todoapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @ColumnInfo("color")
    var color:Long,
    @ColumnInfo("todo")
    var todo: String,
    @ColumnInfo("priority")
    var priority: Int,
    @ColumnInfo("isDone")
    var isDone: Boolean,
    @ColumnInfo("s_d")
    var shortDescription: String,
    @ColumnInfo("date")
    var dateAdded: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)
