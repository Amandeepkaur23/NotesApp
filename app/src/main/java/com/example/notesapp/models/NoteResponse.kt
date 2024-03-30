package com.example.notesapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NotesTable")
data class NoteResponse(
    val title: String,
    val discription: String,
    val __v: Int,
    @PrimaryKey(autoGenerate = false)
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String
)