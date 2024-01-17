package com.example.noteapp.repository

import com.example.noteapp.databases.NoteDatabase
import com.example.noteapp.models.Notes

class NoteRepository(val db: NoteDatabase) {

    suspend fun insertNote(note: Notes) = db.getNoteDao().insert(note)

    suspend fun deleteMore(vararg note: Notes): Boolean {
        val result = db.getNoteDao().deleteMore(*note)
        return result > 0
    }

    fun getAllNote() = db.getNoteDao().getAllNote()

    suspend fun updateNote(id: Int?, title: String?, note: String?) = db.getNoteDao().update(id, title, note)
}