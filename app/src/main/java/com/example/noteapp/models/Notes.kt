package com.example.noteapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "notes_table")
@Serializable
data class Notes @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo(name = "title")
    val title:String?,
    @ColumnInfo(name = "note")
    val note:String?,
    @ColumnInfo(name = "date")
    val date:String?,

    @Ignore
    var _selectCheck:Boolean = false


) {

    var selectCheck: Boolean
        get() {
            return _selectCheck
        }
        set(value) {
            _selectCheck = value
        }

}

