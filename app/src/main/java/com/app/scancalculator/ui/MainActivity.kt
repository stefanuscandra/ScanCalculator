package com.app.scancalculator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.ViewModelProvider
import com.app.scancalculator.BuildConfig
import com.app.scancalculator.R
import com.app.scancalculator.databinding.ActivityMainBinding
import com.app.scancalculator.utils.ImageUtils
import com.app.scancalculator.utils.PermissionUtils
import com.app.scancalculator.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var textRecognizerOptions: TextRecognizerOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textRecognizerOptions = TextRecognizerOptions.Builder().build()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.button.setOnClickListener {
            setupPermission()
        }
        setContentView(binding.root)
        setupViewModel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeCameraIntent()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }

            RC_MEDIA_IMAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeGalleryIntent()
                } else {
                    Toast.makeText(this, "Media image permission denied.", Toast.LENGTH_SHORT).show()
                }
            }

            RC_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeGalleryIntent()
                } else {
                    Toast.makeText(this, "External storage permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data")
            handleImageResult(imageBitmap as Bitmap?)
        } else if (requestCode == REQUEST_TAKE_GALLERY && resultCode == RESULT_OK) {
            val imageBitmap = ImageUtils.uriToBitmap(this, data?.data)
            handleImageResult(imageBitmap)
        }
    }

    private fun setupPermission() {
        if (BuildConfig.FEATURE == CAMERA) {
            PermissionUtils.checkCameraPermission(this) {
                dispatchTakeCameraIntent()
            }
        } else {
            PermissionUtils.checkExternalStoragePermission(this) {
                dispatchTakeGalleryIntent()
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.getBitmap().observe(this) {
            binding.imageView.setImageBitmap(it)
        }
        mainViewModel.getEquationText().observe(this) {
            binding.textEquationValue.text = it
        }
        mainViewModel.getResultText().observe(this) {
            binding.textResultValue.text = it
        }
    }

    private fun dispatchTakeCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takeCameraIntent ->
            takeCameraIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeCameraIntent, REQUEST_TAKE_CAMERA)
            }
        }
    }

    private fun dispatchTakeGalleryIntent() {
        Intent(MediaStore.ACTION_PICK_IMAGES, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { takeGalleryIntent ->
            takeGalleryIntent.type = "image/*"
            takeGalleryIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeGalleryIntent, REQUEST_TAKE_GALLERY)
            }
        }
    }

    private fun handleImageResult(bitmap: Bitmap?) {
        mainViewModel.setBitmap(bitmap)
        readTextFromImage(bitmap)
    }

    /**
     * text recognition from image using Google ML Kit
     * reference : https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=id
     * */
    private fun readTextFromImage(bitmap: Bitmap?) {
        val textRecognition = TextRecognition.getClient(textRecognizerOptions)
        bitmap?.let { bm ->
            val inputImage = InputImage.fromBitmap(bm, 0);
            textRecognition.process(inputImage).addOnSuccessListener {
                readMathExpressionFromImage(it.text)
            }.addOnFailureListener {
                mainViewModel.setEquationText(getString(R.string.failed_recognize_text))
                mainViewModel.setResultText(getString(R.string.failed_recognize_text))
            }
        }
    }

    /**
     * validate string from image recognition to validate whether it is math expression or not
     * by checking per character and save it as first operand, second operand, and an operator
     * constraint :
     *  - only very simple 2 argument operations
     *  - operator that must be supported  +,-,*,/
     * */
    private fun readMathExpressionFromImage(text: String) {
        var firstOperand = ""
        var secondOperand = ""
        var isFirstOperand = true
        var operator = ""
        var isOperatorFound = false
        var isMathExpression = true

        if (text.isNotEmpty()) {
            for (char: Char in text.lowercase()) {
                if (char == ' ' || char == '\n') {
                    // to handle space between expression and escape sequence
                    continue
                } else if (char in '0'..'9') {
                    if (isFirstOperand) firstOperand += char
                    else secondOperand += char
                } else if (char == '+' || char == '-' || char == '*' || char == 'x' || char == '/') {
                    if (!isOperatorFound) {
                        isFirstOperand = false
                        isOperatorFound = true
                        operator += char
                    } else {
                        isMathExpression = false
                        break
                    }
                } else {
                    isMathExpression = false
                    break
                }
            }
        } else {
            isMathExpression = false
        }

        if (firstOperand.isEmpty() || secondOperand.isEmpty() || operator.isEmpty()) isMathExpression = false

        if (isMathExpression) {
            val equation = "$firstOperand$operator$secondOperand"
            val result = calculateMathExpression(firstOperand, secondOperand, operator)
            mainViewModel.setEquationText(equation)
            if (result != null) {
                mainViewModel.setResultText(result.toString())
            }
        } else {
            mainViewModel.setEquationText(getString(R.string.invalid_math_expression))
            mainViewModel.setResultText(getString(R.string.invalid_math_expression))
        }
    }

    /**
     * calculate math expression with valid first operand, second operand, and an operator
     * */
    private fun calculateMathExpression(first: String, second: String, operator: String): Int? {
        return when (operator) {
            "+" -> first.toInt() + second.toInt()
            "-" -> first.toInt() - second.toInt()
            "*" -> first.toInt() * second.toInt()
            "x" -> first.toInt() * second.toInt()
            "/" -> first.toInt() / second.toInt()
            else -> null
        }
    }

    companion object {
        const val CAMERA = "Camera"
        const val RC_CAMERA = 100
        const val RC_MEDIA_IMAGE = 101
        const val RC_EXTERNAL_STORAGE = 102
        const val REQUEST_TAKE_CAMERA = 200
        const val REQUEST_TAKE_GALLERY = 201
    }
}