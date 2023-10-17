package com.app.scancalculator.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel: ViewModel() {
    private val _bitmap = MutableLiveData<Bitmap>()
    private val bitmap: LiveData<Bitmap> = _bitmap

    fun setBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    fun getBitmap(): LiveData<Bitmap> = bitmap
}