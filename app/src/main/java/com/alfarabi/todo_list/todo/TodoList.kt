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
    var note: String,

    @ColumnInfo(name = "tanggal_buat")
    var tanggalBuat: String,

    @ColumnInfo(name = "tanggal_update")
    var tanggalUpdate: String,

    @ColumnInfo(name = "tanggal_tenggat")
    var tenggat: String,

    @ColumnInfo(name = "waktu_tenggat")
    var waktuTenggat: String,

    @ColumnInfo(name = "notifikasi")
    var notifikasi: Boolean = true
)