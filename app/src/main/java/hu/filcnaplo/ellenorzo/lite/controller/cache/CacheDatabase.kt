package hu.filcnaplo.ellenorzo.lite.controller.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.filcnaplo.ellenorzo.lite.controller.cache.dao.EvaluationListDao
import hu.filcnaplo.ellenorzo.lite.controller.cache.dao.MessageListDao
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation

@Database(entities = [Evaluation::class, MessageDescriptor::class], version = 1)
@TypeConverters(MessageDescriptor.MessageDescriptorTypeConverter::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun evalListDao(): EvaluationListDao
    abstract fun messageListDao(): MessageListDao
}
