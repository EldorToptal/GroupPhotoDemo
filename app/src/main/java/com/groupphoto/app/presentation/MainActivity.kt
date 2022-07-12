package com.groupphoto.app.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.snackbar.Snackbar
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ActivityMainBinding
import com.groupphoto.app.presentation.base.BaseActivity
import com.groupphoto.app.worker.UploadWorker
import com.groupphoto.app.util.extensions.SnackbarType
import com.groupphoto.app.util.extensions.showSnackbar
import permissions.dispatcher.*

@RuntimePermissions
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment

    companion object {
        const val IS_FROM_AUTH = "isFromAuth"

        fun getInstance(activity: Activity, isFromAuth: Boolean = true) =
            Intent(activity, MainActivity::class.java).apply {
                putExtra(IS_FROM_AUTH, isFromAuth)
            }
    }

    override fun init(savedInstanceState: Bundle?) {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navContainer) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.profileFragment))
//        if(intent.extras?.getBoolean(IS_FROM_AUTH) == true)
        navigateToBackUpOptionsWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun navigateToBackUpOptions() {
        if (intent.extras?.getBoolean(IS_FROM_AUTH) == true)
            navController.navigate(R.id.backUpOptionsFragment)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForCamera(request: PermissionRequest) {
        showSnackbar(binding.root, Snackbar.LENGTH_LONG, SnackbarType.ERROR,
            "Please, enable read storage permission", "Enable", {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
            })
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onExternalStorageDenied() {
        showSnackbar(binding.root, Snackbar.LENGTH_LONG, SnackbarType.ERROR,
            "Please, enable read storage permission", "Enable", {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
            })
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onExternalStorageNeverAskAgain() {
        showSnackbar(binding.root, Snackbar.LENGTH_LONG, SnackbarType.ERROR,
            "Please, enable read storage permission", "Enable", {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
            })
    }

    override fun onResume() {
        if (!intent.getBooleanExtra(IS_FROM_AUTH, false))
            UploadWorker.triggerImageUploadingWorker(applicationContext)
        super.onResume()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}