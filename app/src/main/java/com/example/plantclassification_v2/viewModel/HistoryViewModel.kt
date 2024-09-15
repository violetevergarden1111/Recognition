package com.example.plantclassification_v2.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantclassification_v2.HistoryDataBase.RecognitionRecord
import com.example.plantclassification_v2.usecase.HistorySaver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historySaver: HistorySaver): ViewModel(){
    val historyList = mutableStateListOf<RecognitionRecord?>()
    private val allRecord = mutableListOf<RecognitionRecord?>()
    private val isDeleting = mutableStateOf(false)
    val deleteList = mutableStateListOf<Long?>()
    val selectedList = mutableStateListOf<Boolean?>()
    private var shownIndex:Int = 0
    private val onceLoadNumber:Int = 10
    val isSelectedAll = mutableStateOf(false)
    val okToDelete = mutableStateOf(false)
    init {
        getAll()
    }
    fun isOkToDelete(){
        var  i =0
        while (i<selectedList.size){
            if (selectedList[i] == true){
                okToDelete.value = true
                return
            }
            i+=1
        }
        okToDelete.value = false
    }
    fun initPage(flag:Boolean){
        if (flag){
            if (isDelete()){
                isDeleting.value = false
                selectedList.clear()
            }
        }
    }
    fun isFull():Boolean{
        return historyList.size >= allRecord.size
    }

    fun loadMore(){
            var i = shownIndex
            while(i< min(allRecord.size,shownIndex+onceLoadNumber)){
                historyList.add(allRecord[i])
                selectedList.add(isSelectedAll.value)
                i+=1
            }
            shownIndex = i
    }
    fun isSelectedAll(){
        var i = 0
        while (i<selectedList.size){
            if(selectedList[i] == false){
                isSelectedAll.value = false
                return
            }
            i+=1
        }
        isSelectedAll.value = true
        okToDelete.value = true
    }

    fun selectAll(){
        var i = 0
        while (i<selectedList.size){
            selectedList[i] = true
            i+=1
        }
        isSelectedAll.value = true
        okToDelete.value = true
    }
    fun overSelectAll(){
        var i = 0
        while (i<selectedList.size){
            selectedList[i] = false
            i+=1
        }
        isSelectedAll.value = false
        okToDelete.value = false
    }
    fun isDelete():Boolean{
        return isDeleting.value
    }
    fun launchDelete(){
        isDeleting.value = true
        var i = 0
        while (i<historyList.size){
            selectedList.add(false)
            i+=1
        }
        isSelectedAll.value = false
        okToDelete.value = false
    }
    fun overDelete(){
        isDeleting.value = false
        deleteList.clear()
        selectedList.clear()
    }
    fun buildDeleteList(){
        viewModelScope.launch {
            var i = 0
            while (i<selectedList.size){
                if (selectedList[i] == true){
                    deleteList.add(historyList[i]?.id)
                }
                i+=1
            }
        }
    }
    fun deleteItem(list: List<Long>){
        viewModelScope.launch{
            historySaver.delete(list)
            deleteList.forEach { itemId->
                var i = 0
                while (i<historyList.size){
                    if (historyList[i] == null){
                        historyList.removeAt(i)
                        allRecord.removeIf{
                            it == null
                        }
                    }
                    else if (historyList[i]?.id==itemId){
                        historyList.removeAt(i)
                        allRecord.removeIf{
                            it?.id == itemId
                        }
                        break
                    }
                    i+=1
                }
            }
            deleteList.clear()
            selectedList.clear()
            isDeleting.value = false
        }
    }
    fun getAll(){
        viewModelScope.launch {
            historySaver.getAllRecord().forEach {
                allRecord.add(it)
            }
            allRecord.removeIf{
                it==null
            }
            var i = shownIndex
            while(i< min(allRecord.size,shownIndex+onceLoadNumber)){
                historyList.add(allRecord[i])
                i+=1
            }
            shownIndex = i
        }
    }

    fun insert(recognitionRecord: RecognitionRecord){
        viewModelScope.launch {
            historySaver.insert(recognitionRecord)
            historyList.add(recognitionRecord)
        }
    }
}