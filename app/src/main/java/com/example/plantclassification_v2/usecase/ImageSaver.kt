package com.example.plantclassification_v2.usecase

import android.net.Uri
import com.example.plantclassification_v2.repository.MyRepository
import javax.inject.Inject

class ImageSaver @Inject constructor(
    private val repo:MyRepository
) {
    fun execute():Uri{
        return repo.provideImageUri()
    }
}