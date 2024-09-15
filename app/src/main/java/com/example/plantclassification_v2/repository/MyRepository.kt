package com.example.plantclassification_v2.repository

import android.content.Context
import android.net.Uri
import com.example.plantclassification_v2.HistoryDataBase.HistoryDao
import com.example.plantclassification_v2.HistoryDataBase.RecognitionRecord
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyRepository @Inject constructor(
    private val dao: HistoryDao,
    @ApplicationContext private val context: Context
) {
    suspend fun getAllRecord(): List<RecognitionRecord> {
        return withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    suspend fun insert(record: RecognitionRecord?): Boolean {
        return withContext(Dispatchers.IO) {
            if (record == null) {
                false
            } else {
                dao.insert(record)
                true
            }
        }
    }


    suspend fun delete(idList:List<Long>){
        withContext(Dispatchers.IO){
            idList.forEach{
                    dao.delete(it)
            }
        }
    }

    suspend fun queryRecord(uid:Long):RecognitionRecord{
        return withContext(Dispatchers.IO){
            dao.queryRecord(uid)
        }
    }
    fun provideImageUri():Uri{
        return ComposeFileProvider.getImageUri(context)
    }
}

