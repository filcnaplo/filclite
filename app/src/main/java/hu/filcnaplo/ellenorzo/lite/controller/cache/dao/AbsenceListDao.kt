package hu.filcnaplo.ellenorzo.lite.controller.cache.dao

import androidx.room.*
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note

@Dao
interface AbsenceListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(absenceList: List<Absence>)

    @Query("SELECT * FROM absences")
    suspend fun getAll(): List<Absence>

    @Delete
    suspend fun deleteList(absenceList: List<Absence>)
}