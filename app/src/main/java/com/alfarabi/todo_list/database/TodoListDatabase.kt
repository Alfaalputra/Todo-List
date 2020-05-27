package com.alfarabi.todo_list.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alfarabi.todo_list.todo.TodoList

@Database(entities = [TodoList::class], exportSchema = false, version = 1)
abstract class TodoListDatabase : RoomDatabase(){

    abstract fun todoListDao(): TodoListDao
    companion object{
        private const val DB_NAME = "TodoList_DB"
        private var instance: TodoListDatabase? = null

        fun getInstance(context: Context): TodoListDatabase?{
            if (instance == null)
                synchronized(TodoListDatabase::class){
                    instance = Room.databaseBuilder(context, TodoListDatabase::class.java, DB_NAME).build()
                }
            return instance
        }
    }
}