package com.alfarabi.todo_list.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.alfarabi.todo_list.database.TodoListRepository

class TodoListViewModel(application: Application) : AndroidViewModel(application) {
    private var todolistRepository = TodoListRepository(application)
    private var todolists: LiveData<List<TodoList>>? = todolistRepository.getTodoLists()

    fun insertTodoList(todoList: TodoList){
        todolistRepository.insert(todoList)
    }

    fun getTodolists(): LiveData<List<TodoList>>?{
        return todolists
    }

    fun deleteTodoList(todoList: TodoList) {
        todolistRepository.delete(todoList)
    }

    fun updateTodoList(todoList: TodoList) {
        todolistRepository.update(todoList)
    }
}