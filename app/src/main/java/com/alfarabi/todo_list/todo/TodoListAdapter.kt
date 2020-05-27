package com.alfarabi.todo_list.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfarabi.todo_list.R
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_todolist.view.*

class TodoListAdapter(private val listener: (TodoList, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_EMPTY = 0
    private val VIEW_TODOLIST = 1
    private var todoList = listOf<TodoList>()

    fun setTodoList(todoList: List<TodoList>){
        this.todoList = todoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            VIEW_TODOLIST -> TodoListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todolist, parent, false))
            VIEW_EMPTY -> EmptyListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_empty, parent, false))
            else -> throw throw IllegalArgumentException("Tipe View Tidak Terdefinisi")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (todoList.isEmpty())
            VIEW_EMPTY
        else
            VIEW_TODOLIST
    }

    override fun getItemCount(): Int = if (todoList.isEmpty()) 1 else todoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_EMPTY -> {
                val emptyHolder = holder as EmptyListViewHolder
                emptyHolder.bindItem()
            }
            VIEW_TODOLIST -> {
                val todolistHolder = holder as TodoListViewHolder
                todolistHolder.bindItem(todoList[position], listener)
            }
        }
    }

    class TodoListViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(todoList: TodoList, listener: (TodoList, Int) -> Unit) {
            itemView.tv_title.text = todoList.judul
            itemView.tv_note.text = todoList.note

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