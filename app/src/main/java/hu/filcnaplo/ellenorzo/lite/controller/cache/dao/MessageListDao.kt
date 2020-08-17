package hu.filcnaplo.ellenorzo.lite.controller.cache.dao

import androidx.room.*
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor

@Dao
interface MessageListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(messages: List<MessageDescriptor>)

    @Query("SELECT * FROM messages WHERE type = :type")
    suspend fun getAllWithType(type: MessageDescriptor.Type): List<MessageDescriptor>

    @Delete
    suspend fun deleteList(messages: List<MessageDescriptor>)
}