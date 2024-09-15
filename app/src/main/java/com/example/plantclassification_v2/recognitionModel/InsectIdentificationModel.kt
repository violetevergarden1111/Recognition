package com.example.plantclassification_v2.recognitionModel

import android.content.res.AssetManager
import android.graphics.Bitmap
import javax.inject.Inject

class InsectIdentificationModel @Inject constructor() {
    var label:String = ""
    class Obj {
        var idx:Int = 0
        var chineseName:String = ""
        var latinName :String = ""
        var prob:Float = 0.0f
    }
    public external fun init(mgr: AssetManager):Boolean
    public external fun detect(bitmap: Bitmap):Array<InsectIdentificationModel.Obj>
    companion object{
        init {
            System.loadLibrary("plantclassification_v2")
        }
    }
}