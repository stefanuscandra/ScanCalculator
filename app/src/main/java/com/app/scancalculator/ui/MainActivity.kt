package com.app.scancalculator.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.app.scancalculator.BuildConfig
import com.app.scancalculator.databinding.ActivityMainBinding
import com.app.scancalculator.utils.PermissionUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            setupPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin kamera diberikan.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
                }
            }

            RC_MEDIA_IMAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin media image diberikan.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Izin media image ditolak.", Toast.LENGTH_SHORT).show()
                }
            }

            RC_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin external storage diberikan.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Izin external storage ditolak.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupPermission() {
        if (BuildConfig.FEATURE == CAMERA) {
            PermissionUtils.checkCameraPermission(this) {
                // TODO: open camera
            }
        } else {
            PermissionUtils.checkExternalStoragePermission(this) {
                // TODO: open gallery
            }
        }
    }

    companion object {
        const val CAMERA = "Camera"
        const val RC_CAMERA = 100
        const val RC_MEDIA_IMAGE = 101
        const val RC_EXTERNAL_STORAGE = 102
    }
}