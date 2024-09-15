package com.example.plantclassification_v2.module

import android.content.Context
import androidx.room.Room
import com.example.plantclassification_v2.HistoryDataBase.HistoryDao
import com.example.plantclassification_v2.HistoryDataBase.HistoryDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@InstallIn(SingletonComponent::class)
@Module
object AppModule{
    @Singleton
    @Provides
    fun provideHistoryDataBase(@ApplicationContext context: Context): HistoryDataBase {
        return Room.databaseBuilder(
            context = context,
            HistoryDataBase::class.java,
            "recognitionRecord.db"
        )
            .build()
    }
    @Singleton
    @Provides
    fun provideHistoryDao(db:HistoryDataBase): HistoryDao {
        return db.historyDao()
    }
}