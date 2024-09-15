package com.example.plantclassification_v2.usecase

import android.content.Context
import com.example.plantclassification_v2.HistoryDataBase.RecognitionRecord
import com.example.plantclassification_v2.repository.MyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HistorySaver @Inject constructor(
    private val repo:MyRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun getAllRecord():List<RecognitionRecord>{
        return repo.getAllRecord()
    }
    suspend fun insert(record: RecognitionRecord?): Boolean{
        return repo.insert(record = record)
    }
    suspend fun delete(idList:List<Long>){
        repo.delete(idList = idList)
    }
    suspend fun queryRecord(uid:Long):RecognitionRecord{
        return repo.queryRecord(uid)
    }
}