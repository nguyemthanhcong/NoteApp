package com.example.noteapp.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.models.Notes

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Notes)

    @Delete
    suspend fun deleteMore(vararg notes: Notes): Int //Xóa nhiều note trong 1 lần

    @Delete
    suspend fun deleteOne( notes: Notes)//Chỉ xóa được một trong 1 lần

    @Query("SELECT * FROM notes_table order by id asc")
    fun getAllNote(): MutableList<Notes>

    @Query("UPDATE notes_table SET title = :title, note = :note WHERE id = :id")
    suspend fun update(id: Int?, title: String?, note: String?)
}