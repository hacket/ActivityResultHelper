package me.hacket.activityresulthelper

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.hacket.activityresulthelper.databinding.ActivityMainBinding
import me.hacket.library.*
import java.io.File

class MainActivity : AppCompatActivity(), IActivityResultProvider {

    companion object {
        private const val TAG = "hacket"
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnRequestOnePermission.setOnClickListener {
            requestPermission(Manifest.permission.CAMERA) {
                Toast.makeText(applicationContext, "请求${it.first}：${it.second}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnRequestOtherPermission.setOnClickListener {
            requestPermission(Manifest.permission.READ_CALENDAR) {
                Toast.makeText(applicationContext, "请求${it.first}：${it.second}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnRequestMorePermission.setOnClickListener {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    "请求${Manifest.permission.CAMERA}:${it[Manifest.permission.CAMERA]}，请求${Manifest.permission.RECORD_AUDIO}:${it[Manifest.permission.RECORD_AUDIO]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnStartActivityForResult.setOnClickListener {
            val intent = Intent(this, ActivityResultsDestinationActivity::class.java)
            intent.putExtra("from", "MainActivity")
            intent.putExtra("input", 12306)
            startActivityForResultExt(intent) {
                val d = it.getStringExtra("data") ?: ""
                binding.tvResult.append("[onActivityResult], data=$d\n")
            }
        }
        binding.btnTakePic.setOnClickListener {
            requestCameraPermission {
                if (it.second) {
                    val imageFile = File(getExternalFilesDir("image"), "temp.jpg")
                    val imageUri = Uri.fromFile(imageFile)
                    val uri = FileProviderUtils.getUriForFile(this, imageFile)
                    takePicture(uri) {
                        Log.d(TAG, "takePicture it=$it, imageUri=$imageUri, uri=$uri")
                    }
                } else {
                    Toast.makeText(applicationContext, "no camera permission", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.btnTakeVideo.setOnClickListener {
            requestCameraPermission {
                if (it.second) {
                    val videoFile = File(getExternalFilesDir("image"), "temp.mp4")
                    val videoUri = Uri.fromFile(videoFile)
                    val uri = FileProviderUtils.getUriForFile(this, videoFile)
                    takeVideo(uri) { u->
                        Log.d(TAG, "takeVideo u=$u, uri=$uri")
                    }
                } else {
                    Toast.makeText(applicationContext, "no camera permission", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.btnTakeVideoCoroutine.setOnClickListener {
            lifecycleScope.launch {
                if (!requestPermissionSuspend(Manifest.permission.CAMERA)) {
                    return@launch
                }
                val videoFile = File(getExternalFilesDir("image"), "temp.mp4")
                val uri = FileProviderUtils.getUriForFile(applicationContext, videoFile)
                takeVideo(uri) {
                    Log.d(TAG, "takeVideo it=$it, uri=$uri")
                }
            }
        }
    }

}