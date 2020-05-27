package com.alfarabi.todo_list.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alfarabi.todo_list.todo.TodoList

@Dao
interface TodoListDao {
    @Query("SELECT * from todoList ")
    fun getTodoList(): LiveData<List<TodoList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoList(todoList: TodoList)

    @Delete
    suspend fun deleteTodoList(todoList: TodoList)

    @Update
    suspend fun updateTodoList(todoList: TodoList)
}