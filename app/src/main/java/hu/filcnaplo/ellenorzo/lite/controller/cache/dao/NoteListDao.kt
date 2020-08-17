package hu.filcnaplo.ellenorzo.lite.controller.cache.dao

import androidx.room.*
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note

@Dao
interface NoteListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(noteList: List<Note>)

    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<Note>

    @Delete
    suspend fun deleteList(noteList: List<Note>)
}