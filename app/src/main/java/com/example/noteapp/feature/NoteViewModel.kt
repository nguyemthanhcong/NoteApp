package com.example.noteapp.feature

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.models.Notes
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    val noteRepository: NoteRepository
):AndroidViewModel(app) {
    fun insertNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insertNote(note)
    }

//    fun deleteNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
//        noteRepository.deleteMore(note)
//    }

    fun getAllNote() = noteRepository.getAllNote()

    fun updateNote(id: Int?, title:String?, note:String?) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(id, title, note)
    }


}