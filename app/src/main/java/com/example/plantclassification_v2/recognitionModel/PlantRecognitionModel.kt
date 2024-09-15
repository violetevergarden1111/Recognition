package com.example.plantclassification_v2.recognitionModel

import android.content.res.AssetManager
import android.graphics.Bitmap
import javax.inject.Inject

class PlantRecognitionModel @Inject constructor() {
    public var label:String =""
    public class PlantObj{
        public var idx:Int = -1
        public var chineseName:String = ""
        public var latinName:String = ""
        public var prob:Float = 0.0f
    }
    public external fun init(mgr: AssetManager):Boolean
    public external fun detect(bitmap: Bitmap):Array<PlantObj>
    companion object{
        init {
            System.loadLibrary("plantclassification_v2")
        }
    }
}