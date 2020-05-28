package com.alfarabi.todo_list

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
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

    companion object{
        var sortTanggalBuat = true
    }

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

        view.tenggat.setOnClickListener{
            Common.showDatePickerDialog(this, view.tenggat)
        }

        view.waktu.setOnClickListener{
            Common.showTimePickerDialog(this, view.waktu)
        }

        val dialogTitle = "Tambah Data"
        val toastMessage =  "Data Berhasil Ditambahkan"
        val failAlertMessage = "Tolong Isi Semua Fild"

        Dialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val note = view.input_note.text.toString()
            val tanggal = view.tenggat.text.toString().trim()
            val waktu = view.waktu.text.toString().trim()

            if (title == "" || tanggal == "" || waktu == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            }
            else {
                val parsedTanggal = Common.convertStringToDate("dd-MM-yy", tanggal)
                val tenggat = Common.formatDate(parsedTanggal, "dd-MM-yy")
                val currentDate = Common.getCurrentDateTime()
                val tanggal = Common.formatDate(currentDate, "dd-MM-yy HH:mm:ss")

                val todo = TodoList(
                    judul = title,
                    note = note,
                    tanggalBuat = tanggal,
                    tanggalUpdate = tanggal,
                    tenggat = tenggat,
                    waktuTenggat = waktu
                )
                todoListViewModel.insertTodoList(todo)

                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }.show()
    }

    private fun showEditDialog(todoList: TodoList) {
        val view = LayoutInflater.from(this).inflate(R.layout.todolist_fragment, null)

        view.tenggat.setOnClickListener{
            Common.showDatePickerDialog(this, view.tenggat)
        }

        view.waktu.setOnClickListener{
            Common.showTimePickerDialog(this, view.waktu)
        }

        view.input_title.setText(todoList.judul)
        view.input_note.setText(todoList.note)
        view.tenggat.setText(todoList.tenggat)
        view.waktu.setText(todoList.waktuTenggat)

        val dialogTitle = "Edit data"
        val toastMessage = "Data Terubah"
        val failAlertMessage = "Tolong isi field Judul"

        Dialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val note = view.input_note.text.toString()
            val waktu = view.waktu.text.toString().trim()
            val tenggat = view.tenggat.text.toString().trim()
            val tanggalBuat = todoList.tanggalBuat

            if (title == "" || tenggat == "" || waktu == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parssedTanggal = Common.convertStringToDate("dd-MM-yy", tenggat)
                val tenggat = Common.formatDate(parssedTanggal, "dd-MM-yy")
                val currentDate = Common.getCurrentDateTime()
                val tanggalUpdate = Common.formatDate(currentDate, "dd-MM-yy HH:mm:ss")

                todoList.judul= title
                todoList.note = note
                todoList.tanggalBuat = tanggalBuat
                todoList.tanggalUpdate = tanggalUpdate
                todoList.tenggat = tenggat
                todoList.waktuTenggat = waktu

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
        val tenggat = "Tenggat : ${todoList.tenggat} ${todoList.waktuTenggat}"
        val tanggalBuat = "Dibuat Pada : ${todoList.tanggalBuat}"
        val tanggalUpdate = "Di Update Pada : ${todoList.tanggalUpdate}"

        val strMessage = "$title\n$note\n$tanggalBuat\n$tanggalUpdate\n$tenggat"

        AlertDialog.Builder(this).setMessage(strMessage).setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu.findItem(R.id.search)).actionView as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Cari Judul"
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                todoListAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoListAdapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.sort -> true
            R.id.tanggal_buat -> {
                sortTanggalBuat = true
                refreshData()
                true
            }
            R.id.tenggat_waktu -> {
                sortTanggalBuat = false
                refreshData()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}