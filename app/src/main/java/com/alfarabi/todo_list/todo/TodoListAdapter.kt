package com.alfarabi.todo_list.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.alfarabi.todo_list.MainActivity
import com.alfarabi.todo_list.R
import com.alfarabi.todo_list.reminder.Common
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_todolist.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoListAdapter(private val listener: (TodoList, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val VIEW_EMPTY = 0
    private val VIEW_TODOLIST = 1
    private var todoList = listOf<TodoList>()
    private var todoListSearch = listOf<TodoList>()

    fun setTodoList(todoList: List<TodoList>){
        this.todoList = todoList
        todoListSearch = todoList
        notifyDataSetChanged()
    }

     override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val keywords = constraint.toString()
                if (keywords.isEmpty())
                    todoListSearch = todoList
                else {
                    val filteredList = ArrayList<TodoList>()
                    for (todo in todoList) {
                        if (todo.toString().toLowerCase(Locale.ROOT).contains(
                                keywords.toLowerCase(Locale.ROOT)
                            )
                        )
                            filteredList.add(todo)
                    }
                    todoListSearch = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = todoListSearch
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                todoListSearch = results?.values as List<TodoList>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            VIEW_TODOLIST -> TodoListViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_todolist, parent, false))
            VIEW_EMPTY -> EmptyListViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_empty, parent, false))
            else -> throw throw IllegalArgumentException("Tipe View Tidak Terdefinisi")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (todoListSearch.isEmpty())
            VIEW_EMPTY
        else
            VIEW_TODOLIST
    }

    override fun getItemCount(): Int = if (todoListSearch.isEmpty()) 1 else todoListSearch.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_EMPTY -> {
                val emptyHolder = holder as EmptyListViewHolder
                emptyHolder.bindItem()
            }
            VIEW_TODOLIST -> {
                val todolistHolder = holder as TodoListViewHolder
                val sortedList = todoListSearch.sortedWith(
                if (MainActivity.sortTanggalBuat)
                    compareBy({it.tanggalBuat}, {it.tanggalUpdate})
                else{
                    compareBy({it.tenggat}, {it.waktuTenggat})
                })
                todolistHolder.bindItem(sortedList[position], listener)
            }
        }
    }

    class TodoListViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(todoList: TodoList, listener: (TodoList, Int) -> Unit) {
            val parsedTanggalBuat = SimpleDateFormat("dd-MM-yy", Locale.US).parse(
                todoList.tanggalBuat) as Date
            val tanggalBuat = Common.formatDate(parsedTanggalBuat, "dd MM yyyy")

            val parsedTanggalUpdate = SimpleDateFormat("dd-MM-yy", Locale.US).parse(
                todoList.tanggalUpdate) as Date
            val tanggalUpdate = Common.formatDate(parsedTanggalUpdate, "dd MM yyyy")

            val date = if (todoList.tanggalUpdate != todoList.tanggalBuat) "di update pada $tanggalUpdate"
                else "Dibuat pada $tanggalBuat"
            val parsedTenggat = SimpleDateFormat("dd-MM-yy", Locale.US).parse(todoList.tenggat) as Date
            val tenggat = Common.formatDate(parsedTenggat, "dd MM yyyy")
            val tenggatWaktu = "Tenggat ${tenggat} ${todoList.waktuTenggat}"

            itemView.tv_title.text = todoList.judul
            itemView.tv_note.text = todoList.note
            itemView.tv_tenggat.text = tenggatWaktu
            itemView.tv_tanggal.text = date

            itemView.setOnClickListener{
                listener(todoList, layoutPosition)
            }
        }
    }

    class EmptyListViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(){
            itemView.empty.text = "Tidak Ada Data"
        }
    }
}