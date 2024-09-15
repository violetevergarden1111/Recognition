package com.example.plantclassification_v2.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.plantclassification_v2.recognitionModel.PlantRecognitionModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class PlantImageDetector @Inject constructor(
    private val plantRecognitionModel: PlantRecognitionModel,
    @ApplicationContext private val context: Context
){
    init {
        plantRecognitionModel.init(context.assets)
    }
    fun execute(imageUri: Uri?):Array<PlantRecognitionModel.PlantObj>{
        val changeImg = decodeUri(imageUri!!).copy(Bitmap.Config.ARGB_8888,true)
        return plantRecognitionModel.detect(changeImg)
    }
    private fun decodeUri(selectedImg: Uri): Bitmap {
        val o: BitmapFactory.Options = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImg),null,o)
        val requiredSize = 640
        var widthTmp:Int = o.outWidth
        var heightTmp:Int = o.outHeight
        var scale = 1
        while (true){
            if (widthTmp/2 < requiredSize||heightTmp/2 < requiredSize){
                break
            }
            widthTmp /= 2
            heightTmp /= 2
            scale *= 2
        }
        val o2: BitmapFactory.Options = BitmapFactory.Options()
        o2.inSampleSize = scale
        val bitmap: Bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImg),null,o2)!!
        var rotate = 0.0f
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(selectedImg)!!
            val exif = ExifInterface(inputStream)
            val orientation:Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            rotate = when(orientation){
                ExifInterface.ORIENTATION_ROTATE_180 -> 180.0f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270.0f
                ExifInterface.ORIENTATION_ROTATE_90 -> 90.0f
                else -> 0.0f
            }
        }
        catch (e: IOException){
            Log.e("MainActivity","ExifInterface IOException")
        }
        val matrix = Matrix()
        matrix.postRotate(rotate)
        val width:Int = bitmap.width
        val height:Int = bitmap.height
        return Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true)
    }
}