package com.example.noteapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.noteapp.models.NoteResponse
import com.example.noteapp.models.Notes
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainViewModel(
    app:Application,
    val noteRepository: NoteRepository
): AndroidViewModel(app) {

    val noteLiveData: MutableLiveData<MutableList<Notes>> = MutableLiveData()
    val result: MutableLiveData<Boolean> = MutableLiveData()

    fun insertNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insertNote(note)
    }

    fun deleteNote(vararg note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        val deleteSuccess = noteRepository.deleteMore(*note)
        getNoteData()
        result.postValue(deleteSuccess)

    }

    fun getNoteData() {
        viewModelScope.launch (Dispatchers.IO) {
            val result: MutableList<Notes> = getAllNote()
            noteLiveData.postValue(result)
        }

    }

    fun getAllNote() = noteRepository.getAllNote()


    fun updateNote(id: Int?, title:String?, note:String?) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(id, title, note)
    }

    fun noteListResult(): MutableList<Notes> {
        return getAllNote()
    }


}