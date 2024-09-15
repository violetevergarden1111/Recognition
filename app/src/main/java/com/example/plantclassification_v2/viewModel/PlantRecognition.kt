package com.example.plantclassification_v2.viewModel


import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantclassification_v2.HistoryDataBase.RecognitionRecord
import com.example.plantclassification_v2.data.RecognitionResult
import com.example.plantclassification_v2.usecase.HistorySaver
import com.example.plantclassification_v2.usecase.ImageSaver
import com.example.plantclassification_v2.usecase.InsectImageDetector
import com.example.plantclassification_v2.usecase.PlantImageDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantRecognition @Inject constructor(
    private val historySaver: HistorySaver,
    private val imageSaver: ImageSaver
): ViewModel() {
    var tag:String = ""
    @Inject
    lateinit var plantImageDetector: PlantImageDetector
    @Inject
    lateinit var insectImageDetector: InsectImageDetector

    val resultList = mutableStateListOf<RecognitionResult?>(null)
    val imageUri = mutableStateOf<Uri?>(null)
    val loadImg = mutableStateOf(false)
    val isDetected = mutableStateOf(false)
    private fun initPageForHistory(recognitionRecord:RecognitionRecord){
        if (resultList.isNotEmpty()){
            resultList.clear()
        }
        imageUri.value = recognitionRecord.imageUri.toUri()
        loadImg.value = true
        isDetected.value = true
        var i = 0
        while (i<recognitionRecord.chineseNameList.size){
            resultList.add(
                RecognitionResult(
                    chineseName =recognitionRecord.chineseNameList[i],
                    latinName = recognitionRecord.latinNameList[i],
                    prob = recognitionRecord.probList[i]
                )
            )
            i+=1;
        }
    }
    fun initPageFromHome(){
        if (resultList.isNotEmpty()){
            resultList.clear()
        }
        imageUri.value = null
        loadImg.value = false
        isDetected.value = false
    }
    fun getPicture(uri: Uri?){
        imageUri.value = uri
    }
    fun getTag(targetTag : String){
        tag = targetTag
    }
    fun plantImageDetect(){
        viewModelScope.launch {
            resultList.clear()
            val objects = plantImageDetector.execute(imageUri = imageUri.value)
            objects.forEach {item->
                val nameList = item.chineseName.split("_")
                resultList.add(RecognitionResult(chineseName = nameList.last(),latinName = item.latinName, prob = item.prob))
            }
            isDetected.value = true
        }

    }
    fun insectImageDetect(){
        viewModelScope.launch {
            resultList.clear()
            val objects = insectImageDetector.execute(imageUri = imageUri.value)
            objects.forEach { item->
                val nameList = item.chineseName.split("_")
                resultList.add(RecognitionResult(chineseName = nameList.last(), latinName = item.latinName, prob = item.prob))
            }
            isDetected.value = true
        }
    }
    fun getChineseNameList():List<String>{
        return buildList {
            resultList.forEach{
                if (it!=null)
                    this.add(it.chineseName)
            }
        }
    }
    fun getLatinNameList():List<String>{
        return buildList {
            resultList.forEach{
                if (it!=null)
                    this.add(it.latinName)
            }
        }
    }
    fun getProbList():List<Float>{
        return buildList {
            resultList.forEach {
                if (it!=null)
                    this.add(it.prob)
            }
        }
    }
    fun queryRecord(historyId:Long){
        viewModelScope.launch {
            val record = historySaver.queryRecord(historyId)
            initPageForHistory(record)
        }
    }
    fun provideUri():Uri{
        return imageSaver.execute()
    }
}