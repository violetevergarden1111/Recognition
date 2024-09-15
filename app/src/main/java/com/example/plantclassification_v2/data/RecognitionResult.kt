package com.example.plantclassification_v2.data

data class RecognitionResult(
    val chineseName:String,
    val latinName:String,
    val prob:Float
)