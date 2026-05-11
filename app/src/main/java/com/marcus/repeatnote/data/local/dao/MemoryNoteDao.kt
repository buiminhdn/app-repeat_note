package com.marcus.repeatnote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marcus.repeatnote.data.local.entity.MemoryNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryNoteDao {

    @Query("SELECT * FROM memory_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<MemoryNoteEntity>>

    @Query("SELECT * FROM memory_notes WHERE category = :category ORDER BY createdAt DESC")
    fun getNotesByCategory(category: String): Flow<List<MemoryNoteEntity>>

    @Query(
        "SELECT * FROM memory_notes WHERE title LIKE '%' || :query || '%' " +
                "OR content LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    fun searchNotes(query: String): Flow<List<MemoryNoteEntity>>

    @Query("SELECT * FROM memory_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): MemoryNoteEntity?

    @Query("SELECT * FROM memory_notes WHERE category = :category ORDER BY createdAt ASC")
    suspend fun getNotesByCategoryAsc(category: String): List<MemoryNoteEntity>

    @Query("SELECT * FROM memory_notes ORDER BY createdAt ASC")
    suspend fun getAllNotesAsc(): List<MemoryNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: MemoryNoteEntity): Long

    @Update
    suspend fun updateNote(note: MemoryNoteEntity)

    @Delete
    suspend fun deleteNote(note: MemoryNoteEntity)
}
