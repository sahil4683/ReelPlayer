package com.reelplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.reelplayer.databinding.ActivitySplashBinding
import com.reelplayer.util.PermissionHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) goToList()
        else handlePermissionDenied()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGrantPermission.setOnClickListener { requestPermission() }

        if (PermissionHelper.hasStoragePermission(this)) goToList()
        else showPermissionUI()
    }

    private fun requestPermission() {
        val permission = PermissionHelper.getRequiredPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, "Storage permission is needed to find videos", Toast.LENGTH_LONG).show()
        }
        permissionLauncher.launch(permission)
    }

    private fun handlePermissionDenied() {
        binding.tvPermissionStatus.text = "Permission denied. Tap the button to grant access."
    }

    private fun showPermissionUI() {
        binding.tvPermissionStatus.text = "ReelPlayer needs access to your videos to get started."
    }

    private fun goToList() {
        startActivity(Intent(this, VideoListActivity::class.java))
        finish()
    }
}
