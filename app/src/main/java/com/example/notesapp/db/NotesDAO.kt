package com.example.notesapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.models.NoteResponse
import retrofit2.Response

@Dao
interface NotesDAO {
    @Insert
    suspend fun addNote(note: List<NoteResponse>)

    @Query("SELECT * FROM NotesTable")
    fun getNotes(): List<NoteResponse>

    @Insert
    suspend fun createNote(noteResponse: NoteResponse)

    @Delete
    suspend fun deleteNote(noteResponse: NoteResponse)

    @Update
    suspend fun updateNote(noteResponse: NoteResponse)

}