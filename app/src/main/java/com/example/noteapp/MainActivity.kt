package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.databases.NoteDatabase
import com.example.noteapp.databinding.ActivityMainBinding
import com.example.noteapp.factory.ViewModelProviderFactory
import com.example.noteapp.feature.AddNote
import com.example.noteapp.models.Notes
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity(), NoteAdapter.NoteItemListener {
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    lateinit var viewModel: MainViewModel
    private lateinit var noteAdapter: NoteAdapter
    private var arrayItemSelect: MutableList<Int> = mutableListOf()
    private var listItemSelected: MutableList<Notes> = mutableListOf()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("GET_RESULT_CODE ", " ${result.resultCode}")
            if (result.resultCode == Constants.ADD_NEW_NOTE_RESULT) {
                coroutineScope.launch(Dispatchers.IO) {
                    noteAdapter.differ.submitList(viewModel.noteListResult())
                }
            } else if (result.resultCode == Constants.UPDATE_NOTE_RESULT) {
                coroutineScope.launch(Dispatchers.IO) {
                    noteAdapter.differ.submitList(viewModel.noteListResult())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coroutineScope.launch(Dispatchers.IO) {
            val noteRepository = NoteRepository(NoteDatabase(this@MainActivity))
            val viewModelProviderFactory = ViewModelProviderFactory(application, noteRepository)
            viewModel = ViewModelProvider(
                this@MainActivity,
                viewModelProviderFactory
            )[MainViewModel::class.java]
            setupRecyclerView()
            getNoteData()
        }
        binding.floatingActionButton.setOnClickListener {
            noteAdapter.resetAllWhenAddNewNote()
            hideTopBar()
            openAddNewNote()
        }

        binding.imgDelete.setOnClickListener {
            coroutineScope.launch {
                deleteItemNotes()
                noteAdapter.clearSelectedItemList()
            }

            viewModel.noteLiveData.observe(this) { data ->
                noteAdapter.differ.submitList(data)
                hideTopBar()
                noteAdapter.resetWhenDeleteSuccess()
            }

        }

    }

    suspend fun deleteItemNotes() {
        val job = coroutineScope.launch(Dispatchers.IO) {
            val selectedNotes = mutableListOf<Notes>()
            noteAdapter.getSelectedItemList().forEach {
                selectedNotes.add(it)
            }

            viewModel.deleteNote(*selectedNotes.toTypedArray())
        }
        job.join()
    }

    private fun openAddNewNote() {
        val intent = Intent(this@MainActivity, AddNote::class.java)
        startForResult.launch(intent)
    }

    private fun hideTopBar() {
        binding.layoutBarAction.visibility = View.GONE
    }

    private fun showTopBar() {
        binding.layoutBarAction.visibility = View.VISIBLE
    }


    private fun getNoteData() {
        viewModel.getNoteData()
        noteAdapter.differ.submitList(viewModel.noteListResult())
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(this)
        binding.recyclerView.apply {
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        }
    }

    override fun onClickOpenItemListener(note: Notes) {
        val intent = Intent(this@MainActivity, AddNote::class.java)
        val json = Json.encodeToString(note)
        intent.putExtra("NOTE_JSON", json)
        startForResult.launch(intent)
    }

    override fun onHideTopActionBar() {
        hideTopBar()
    }

    override fun onShowTopActionBar() {
        showTopBar()
    }


}