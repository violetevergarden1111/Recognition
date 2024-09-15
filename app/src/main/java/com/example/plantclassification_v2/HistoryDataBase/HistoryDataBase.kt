package com.example.plantclassification_v2.HistoryDataBase

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@TypeConverters(InspectionConverter::class)
@Entity(tableName = "recognitionhistory")
data class RecognitionRecord(
    @PrimaryKey
    val id:Long,

    val tag:String,
    val imageUri:String,
    val chineseNameList: List<String>,
    val latinNameList:List<String>,
    val probList:List<Float>
)
class InspectionConverter{
    @TypeConverter
    fun stringToObject(value:String):List<String>{
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value,listType)
    }
    @TypeConverter
    fun stringToProb(value: String):List<Float>{
        val listType = object : TypeToken<List<Float>>() {}.type
        return Gson().fromJson(value,listType)
    }
    @TypeConverter
    fun objectToString(list:List<Any>):String{
        return Gson().toJson(list)
    }
}


@Database(
    entities = [RecognitionRecord::class],
    version = 1,
    exportSchema = false
)
abstract class HistoryDataBase: RoomDatabase(){
    abstract fun historyDao():HistoryDao
}
