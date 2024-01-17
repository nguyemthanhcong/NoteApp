package com.example.noteapp.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.MainViewModel
import com.example.noteapp.repository.NoteRepository

class ViewModelProviderFactory(
    val app: Application,
    val noteRepository: NoteRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(app, noteRepository) as T
    }
}