package hu.filcnaplo.ellenorzo.lite.controller.cache.eval

import androidx.room.*
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation

@Dao
interface EvaluationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(evals: List<Evaluation>)

    @Query("SELECT * FROM evals")
    suspend fun getAll(): List<Evaluation>
}