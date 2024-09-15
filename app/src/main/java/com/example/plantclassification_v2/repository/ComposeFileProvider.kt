package com.example.plantclassification_v2.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import javax.inject.Inject

class ComposeFileProvider @Inject constructor():FileProvider() {
    companion object{
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir,"images")
            directory.mkdir()
            val imageTempfile = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            val authority = context.packageName + ".fileprovider"
            return FileProvider.getUriForFile(context, authority, imageTempfile)
        }
    }
}