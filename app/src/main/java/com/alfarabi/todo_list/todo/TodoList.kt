package com.alfarabi.todo_list.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todoList")
data class TodoList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "judul")
    var judul: String,

    @ColumnInfo(name = "note")
    var note: String
)