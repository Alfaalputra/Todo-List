package com.alfarabi.todo_list.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.alfarabi.todo_list.todo.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoListRepository(application: Application) {

    private val todolistDao: TodoListDao?
    private var todoLists: LiveData<List<TodoList>>? = null

    init {
        val db = TodoListDatabase.getInstance(application.applicationContext)
        todolistDao = db?.todoListDao()
        todoLists = todolistDao?.getTodoList()
    }

    fun getTodoLists(): LiveData<List<TodoList>>?{
        return todoLists
    }

    fun insert(todoList: TodoList) = runBlocking {
        this.launch(Dispatchers.IO) {
            todolistDao?.insertTodoList(todoList)
        }
    }

    fun delete(todoList: TodoList){
        runBlocking {
            this.launch(Dispatchers.IO){
                todolistDao?.deleteTodoList(todoList)
            }
        }
    }

    fun update(todoList: TodoList) = runBlocking {
        this.launch(Dispatchers.IO){
            todolistDao?.updateTodoList(todoList)
        }
    }
}