package hu.filcnaplo.ellenorzo.lite.controller.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.filcnaplo.ellenorzo.lite.controller.cache.eval.EvaluationDao
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation

@Database(entities = arrayOf(Evaluation::class), version = 1)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun evalDao(): EvaluationDao
}
