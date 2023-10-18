package com.app.scancalculator.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for main activity view
* */
class MainViewModel : ViewModel() {
    private val bitmapLiveData = MutableLiveData<Bitmap>()
    private val equationTextLiveData = MutableLiveData<String>()
    private val resultTextLiveData = MutableLiveData<String>()

    fun setBitmap(bitmap: Bitmap?) {
        bitmapLiveData.value = bitmap
    }
    fun getBitmap(): LiveData<Bitmap> = bitmapLiveData
    fun setEquationText(text: String?) {
        equationTextLiveData.value = text
    }
    fun getEquationText() : LiveData<String> = equationTextLiveData
    fun setResultText(text: String?) {
        resultTextLiveData.value = text
    }
    fun getResultText(): LiveData<String> = resultTextLiveData
}