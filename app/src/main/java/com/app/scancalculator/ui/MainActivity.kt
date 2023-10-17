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
import com.app.scancalculator.databinding.ActivityMainBinding
import com.app.scancalculator.utils.ImageUtils
import com.app.scancalculator.utils.PermissionUtils
import com.app.scancalculator.viewmodel.ImageViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            setupPermission()
        }
        setupViewModel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeCameraIntent()
                } else {
                    Toast.makeText(this, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
                }
            }

            RC_MEDIA_IMAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeGalleryIntent()
                } else {
                    Toast.makeText(this, "Izin media image ditolak.", Toast.LENGTH_SHORT).show()
                }
            }

            RC_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    dispatchTakeGalleryIntent()
                } else {
                    Toast.makeText(this, "Izin external storage ditolak.", Toast.LENGTH_SHORT).show()
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
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        imageViewModel.getBitmap().observe(this) {
            println("imagedata : $it")
            binding.imageView.setImageBitmap(it)
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
        imageViewModel.setBitmap(bitmap)
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