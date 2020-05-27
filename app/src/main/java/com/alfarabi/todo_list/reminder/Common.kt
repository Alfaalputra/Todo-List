package com.alfarabi.todo_list.reminder

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object Common {
    fun showSelector(
        context: Context,
        title: String,
        items: Array<String>,
        onClick: (DialogInterface, Int) -> Unit
    ) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle(title)
        alertBuilder.setItems(items) { dialog, which ->
            onClick(dialog, which)
        }.show()
    }
}