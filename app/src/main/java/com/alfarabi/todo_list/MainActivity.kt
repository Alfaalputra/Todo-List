package com.alfarabi.todo_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfarabi.todo_list.reminder.Common
import com.alfarabi.todo_list.reminder.ConfirmDialog
import com.alfarabi.todo_list.reminder.Dialog
import com.alfarabi.todo_list.todo.TodoList
import com.alfarabi.todo_list.todo.TodoListAdapter
import com.alfarabi.todo_list.todo.TodoListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.todolist
import kotlinx.android.synthetic.main.todolist_fragment.view.*

class MainActivity : AppCompatActivity(){
    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var todoListAdapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        todolist.layoutManager = layoutManager

        todoListAdapter = TodoListAdapter(){ todoList, _ ->
            val options = resources.getStringArray(R.array.edit_delete)
            Common.showSelector(this, "Choose action", options) { _, i ->
                when (i) {
                    0 -> showDetailsDialog(todoList)
                    1 -> showEditDialog(todoList)
                    2 -> showDeleteDialog(todoList)
                }
            }
        }

        todolist.adapter = todoListAdapter

        todoListViewModel = ViewModelProvider(this).get(TodoListViewModel::class.java)

        refresh_layout.setOnRefreshListener {
            refreshData()
        }

        add.setOnClickListener {
            showInsertDialog()
        }

    }

    override fun onResume() {
        super.onResume()
        observeData()
    }

    private fun observeData(){
        todoListViewModel.getTodolists()?.observe(this, Observer {
            todoListAdapter.setTodoList(it)
            setProgressbarVisibility(false)
        })
    }

    private fun refreshData(){
        setProgressbarVisibility(true)
        observeData()
        refresh_layout.isRefreshing = false
    }

    private fun setProgressbarVisibility(state: Boolean) {
        if (state) progressbar.visibility = View.VISIBLE
        else progressbar.visibility = View.INVISIBLE
    }

    private fun showInsertDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.todolist_fragment, null)

        val dialogTitle = "Add data"

        Dialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val note = view.input_note.text.toString()

            val todo = TodoList(
                judul = title,
                note = note
            )
            todoListViewModel.insertTodoList(todo)
        }.show()
    }

    private fun showEditDialog(todoList: TodoList) {
        val view = LayoutInflater.from(this).inflate(R.layout.todolist_fragment, null)

        view.input_title.setText(todoList.judul)
        view.input_note.setText(todoList.note)

        val dialogTitle = "Edit data"
        val toastMessage = "Data Terubah"
        val failAlertMessage = "Tolong Isi Semua Field"


        Dialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val note = view.input_note.text.toString()

            if (title == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                todoList.judul= title
                todoList.note = note

                todoListViewModel.updateTodoList(todoList)
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteDialog(todoList: TodoList) {
        val dialogTitle = "Hapus"
        val dialogMessage = "Yakin ingin menghapus Data?"
        val toastMessage = "Data berhasil di hapus"
        ConfirmDialog(this, dialogTitle, dialogMessage) {
            todoListViewModel.deleteTodoList(todoList)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }.show()
    }

    private fun showDetailsDialog(todoList: TodoList) {
        val title = "Title: ${todoList.judul}"
        val note = "Note: ${todoList.note}"
        val strMessage = "$title\n$note"

        AlertDialog.Builder(this).setMessage(strMessage).setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }
}