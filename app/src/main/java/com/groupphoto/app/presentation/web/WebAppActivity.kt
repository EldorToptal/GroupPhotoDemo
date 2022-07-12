package com.groupphoto.app.presentation.web

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.presentation.main.AddPhotoFragment
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.groupphoto.app.worker.UploadWorker
import com.pawegio.kandroid.e
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_web_app_landing.*

import org.koin.androidx.viewmodel.ext.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit


class WebAppActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE: Int = 101
    private val viewModel: LandingActivityViewModel by viewModel()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_app_landing)

        webView.loadUrl(getString(R.string.FirebaseBaseUrl) + "register")
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true

        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.pluginState = WebSettings.PluginState.ON

//        webView.settings.useWideViewPort = true
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LandingActivityEvent.TabSwitchRequested -> e("Landing")
            }
        })
        getPermissions()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Activity.RESULT_OK ->
                if (requestCode == PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    UploadWorker.triggerImageUploadingWorker(applicationContext)
                } else toast(data?.data.toString())
        }


    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@WebAppActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, AddPhotoFragment.IMAGE_PICK_CODE)
    }

    fun getPermissions() {
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this@WebAppActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission

                    requestPermissions(permissions, AddPhotoFragment.PERMISSION_CODE)
                } else {
//                    getAllImages()
                    UploadWorker.triggerImageUploadingWorker(applicationContext)
                    //permission already granted
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        } else {
            requestPermission()
        }
    }



    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this@WebAppActivity,
            android.Manifest.permission.CAMERA
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this@WebAppActivity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }



    companion object {
        private const val TAG = "WebAppActivity "
    }
}