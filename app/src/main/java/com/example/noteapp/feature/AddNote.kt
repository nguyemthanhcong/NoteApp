package com.example.noteapp.feature

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.util.Constants
import com.example.noteapp.databases.NoteDatabase
import com.example.noteapp.databinding.ActivityAddNoteBinding
import com.example.noteapp.factory.NoteViewModelProviderFactory
import com.example.noteapp.models.Notes
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNote : AppCompatActivity() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityAddNoteBinding
    lateinit var viewModel: NoteViewModel
    private var noteOldId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val noteRepository = NoteRepository(NoteDatabase(this@AddNote))
        val viewModelProviderFactory = NoteViewModelProviderFactory(application, noteRepository)
        viewModel = ViewModelProvider(this@AddNote, viewModelProviderFactory)[NoteViewModel::class.java]

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgSaved.setOnClickListener {
            val title = binding.edtTitle.text.toString()
            val noteContent = binding.edtNote.text.toString()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            if (title.isEmpty() && noteContent.isEmpty()) {

            } else {
                if (noteOldId != 0) {
                    Log.d("CHECK_LOG_UPDATE_NOTE: ", " $noteOldId")
                    Log.d("CHECK_LOG_UPDATE_NOTE: ", " $title")
                    Log.d("CHECK_LOG_UPDATE_NOTE: ", " $noteContent")
                    viewModel.updateNote(noteOldId, title, noteContent)
                    setResult(Constants.UPDATE_NOTE_RESULT)
                } else {
                    Log.d("CHECK_LOG_INSERT_NOTE: ", "")
                    val note = Notes(null, title, noteContent, dateFormat.format(Date()))
                    viewModel.insertNote(note)
                    setResult(Constants.ADD_NEW_NOTE_RESULT)
                }

                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        val noteJson = intent.getStringExtra("NOTE_JSON")
        val json = noteJson?.let { Json.decodeFromString<Notes>(it) }

        if (json != null) {
            Log.d("CHECK_LOG_NOTE_JSON_ID: ", " ${json.id}")
            noteOldId = json.id!!
            if(!json.title.isNullOrEmpty()) {
                binding.edtTitle.setText(json.title)
            }

            if (!json.note.isNullOrEmpty()) {
                binding.edtNote.setText(json.note)
            }
        }
    }
}