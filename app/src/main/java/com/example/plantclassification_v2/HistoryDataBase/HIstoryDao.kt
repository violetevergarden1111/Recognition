package com.example.plantclassification_v2.HistoryDataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(recognitionRecord: RecognitionRecord):Long

    @Query("SELECT * FROM recognitionhistory")
    suspend fun getAll():List<RecognitionRecord>

    @Query("SELECT * FROM recognitionhistory WHERE id = (:uid)")
    suspend fun queryRecord(uid:Long):RecognitionRecord

    @Query("DELETE FROM recognitionhistory WHERE id = (:uid)")
    suspend fun delete(uid: Long)
}