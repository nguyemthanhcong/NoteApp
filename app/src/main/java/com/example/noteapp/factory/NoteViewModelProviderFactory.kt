package com.example.noteapp.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.feature.NoteViewModel
import com.example.noteapp.repository.NoteRepository

class NoteViewModelProviderFactory(
    val app: Application,
    val noteRepository: NoteRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(app, noteRepository) as T
    }
}