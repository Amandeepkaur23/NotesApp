package com.example.notesapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notesapp.api.NoteAPI
import com.example.notesapp.db.NoteDatabase
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.utils.Constants.TAG
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.utils.NetworkUtils
import com.example.notesapp.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteAPI: NoteAPI,
    private val noteDatabase: NoteDatabase,
    private val networkUtils: NetworkUtils,
    private val appContext: Context
) {
    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _notesLiveData

    //used to set status of create,update and delete request
    private val _statusLiveData = MutableLiveData<NetworkResult<String>>()
    val statusLiveData: LiveData<NetworkResult<String>>
        get() = _statusLiveData

    //Variable to store userId
    private var userId:String? = null

    @Inject
    lateinit var tokenManager: TokenManager

    suspend fun getNotes() {

        if (networkUtils.isInternetAvailable(appContext)) {
            try {
                _notesLiveData.postValue(NetworkResult.Loading())
                val response = noteAPI.getNotes()

                //code is same, so we can move this code to generic class
                if (response.isSuccessful && response.body() != null) {
                    _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
                    //to get userID of the registered user
                    userId = response.body()!![1].userId
                    Log.d(TAG, userId!!)

                    withContext(Dispatchers.IO){
                        val existingNotes = noteDatabase.getNoteDao().getNotes()

                        val newNotes = response.body()!!.filterNot { newNote ->
                            existingNotes.any { existingNote ->
                                newNote._id == existingNote._id
                            }
                        }
                        noteDatabase.getNoteDao().addNote(newNotes)
                    }
                }
                else if (response.errorBody() != null) {
                    val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                    _notesLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
                }
                else {
                    _notesLiveData.postValue(NetworkResult.Error("Something went wrong"))
                }
            }
            catch (e: Exception) {
                _notesLiveData.postValue(NetworkResult.Error(e.message.toString()))
            }
        } else {
            try {
                //Access notes from Database at background
                withContext(Dispatchers.IO){
                    val noteResponse = noteDatabase.getNoteDao().getNotes()
                    val currentUserId = tokenManager.getUserId()

                    Log.d(TAG, " current user id $currentUserId")

                    val authNotes = noteResponse.filter { it.userId == currentUserId }
                    Log.d(TAG, " notes from current user $authNotes")

                    if(authNotes.isNotEmpty()){
                        _notesLiveData.postValue(NetworkResult.Success(authNotes))
                        Log.d(TAG,authNotes.toString())
                    }
                    else{
                        _notesLiveData.postValue(NetworkResult.Error("No notes available offline"))
                    }
                }
            } catch (e: Exception) {
                _notesLiveData.postValue(NetworkResult.Error(e.message.toString()))
            }
        }
    }

    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.createNote(noteRequest)
        handleResponse(response, "Note Created!!")
    }

    suspend fun updateNote(noteId: String, noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.updateNote(noteId, noteRequest)
        handleResponse(response, "Note Updated!!")
    }

    suspend fun deleteNote(noteID: String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.deleteNote(noteID)
        handleResponse(response, "Note Deleted!!")
    }

    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(message))
        } else {
            _statusLiveData.postValue((NetworkResult.Error("Something went wrong")))
        }
    }
}
