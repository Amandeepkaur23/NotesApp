package com.example.notesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.repository.NoteRepository
import com.example.notesapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
   private val noteRepository: NoteRepository
): ViewModel() {

    val noteLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = noteRepository.notesLiveData
    val statusLiveData: LiveData<NetworkResult<String>>
        get() = noteRepository.statusLiveData

    fun getNotes(){
        viewModelScope.launch {
            noteRepository.getNotes()
        }
    }

    fun createNotes(noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.createNote(noteRequest)
        }
    }

    fun updateNotes(noteId: String, noteRequest: NoteRequest){
        viewModelScope.launch {
          noteRepository.updateNote(noteId, noteRequest)
        }
    }

    fun deleteNotes(noteId: String){
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
        }
    }
}