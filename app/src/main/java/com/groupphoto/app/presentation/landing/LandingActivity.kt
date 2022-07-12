package com.groupphoto.app.presentation.landing

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ActivityLandingBinding
import com.groupphoto.app.presentation.base.BaseActivity
import com.groupphoto.app.presentation.dialogs.AddPhotoDialog
import com.groupphoto.app.presentation.main.AddPhotoFragment
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.groupphoto.app.util.Constants
import com.groupphoto.app.util.MainNavigationManager
import com.groupphoto.app.worker.UploadWorker
import com.groupphoto.app.util.extensions.getCompatDrawable
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.tab_nav.view.*
import org.koin.androidx.viewmodel.ext.viewModel
import timber.log.Timber
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import androidx.work.WorkInfo
import java.util.concurrent.ExecutionException


class LandingActivity : BaseActivity<ActivityLandingBinding>(ActivityLandingBinding::inflate) {
    val ACTION = "com.codepath.example.servicesdemo.MyTestService"
    private var allImages: ArrayList<String> = ArrayList<String>()

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, LandingActivity::class.java))
        }
    }

    private val menuTabs = listOf(
        Pair(R.drawable.sample_home_nav_ic, "Activity"),
        Pair(R.drawable.sample_rings_nav_ic, "Rings"),
        Pair(R.drawable.ic_plus_circle, ""),
        Pair(R.drawable.sample_pools_nav_ic, "Pools"),
        Pair(R.drawable.sample_profile_nav_ic, "Me")
    )
    private val PERMISSION_REQUEST_CODE: Int = 101
    private val REQUEST_IMAGE_CAPTURE = 1
    private var mCurrentPhotoPath: String? = null;
    private val viewModel: LandingActivityViewModel by viewModel()

    private val navManager by lazy { MainNavigationManager(this) }

    private val broadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
            if (resultCode == Activity.RESULT_OK) {
                val resultValue = intent.getStringExtra("resultValue")
                Toast.makeText(applicationContext, resultValue, Toast.LENGTH_SHORT).show()
            }
        }
    }

    lateinit var _pools_nav_btn: ImageView
    lateinit var _txvPools: TextView
    lateinit var _profile_nav_btn: ImageView
    lateinit var _txvAccount: TextView

    override fun init(savedInstanceState: Bundle?) {

        navManager.onCreate(savedInstanceState)

        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LandingActivityEvent.TabSwitchRequested -> onTabSwitchRequested(
                    event.tabPosition,
                    event.toDestination,
                    event.inGraph,
                    event.withExceptionDestinations,
                    event.arguments
                )
            }
        })

        _pools_nav_btn = findViewById(R.id.pools_nav_btn)
        _txvPools = findViewById(R.id.txvPools)
        _profile_nav_btn = findViewById(R.id.profile_nav_btn)
        _txvAccount = findViewById(R.id.txvAccount)

        getPermissions()
        getDynamicLink()
        setupMenuTabs()


        binding.bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                //R.id.nav_pools -> main_tabs_bottom_nav.getTabAt(3)?.select()

                R.id.nav_account -> binding.mainTabsBottomNav.getTabAt(4)?.select()

            }

            true
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver);
    }


    private fun getDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Timber.e("email link $deepLink")
                }

            }
            .addOnFailureListener(this) { e -> Timber.d("getDynamicLink:onFailure", e) }
    }

    private fun onTabSwitchRequested(
        tabPosition: Int,
        destination: Int?,
        graphDestination: Int?,
        exceptionDestinations: List<Int>?,
        arguments: Bundle?
    ) {
        // switch tab

        binding.mainTabsBottomNav.getTabAt(tabPosition)?.select()

        // navigate to destination
        navManager.currentController?.apply {
            fun isInExceptionDestinations() =
                exceptionDestinations?.contains(currentDestination?.id) == true

            // try to navigate
            try {
                destination?.let { navigate(it, arguments) }

            } catch (exc: Exception) {
                // fallback to moving to the graph first
                if (graphDestination != null) {
                    // if there's a given graph that holds the destination
                    navigate(graphDestination, arguments) // go to the graph first
                    // then navigate to destination if you're not there yet
                    if (!isInExceptionDestinations()) destination?.let { navigate(it, arguments) }

                } else {
                    // if there's no given graph then pop until you get to the destination
                    while (!isInExceptionDestinations()) popBackStack()
                }
            }
        }
    }

    private fun setupMenuTabs() {


        binding.mainTabsBottomNav.apply {

            menuTabs.mapIndexed { index, tab ->
                addTab(newTab())
                var v: View = View.inflate(context, R.layout.tab_nav, null)
                v.nav_text_title.text = tab.second
                v.nav_icon?.setImageDrawable(context.getCompatDrawable(tab.first))

                getTabAt(index)?.setCustomView(v)

            }

        }

        binding.mainTabsBottomNav.getTabAt(4)?.select()

        /* main_tabs_bottom_nav.getTabAt(3)?.select()
         selectedPools()

         _pools_nav_btn.setOnClickListener {
             selectedPools()
             main_tabs_bottom_nav.getTabAt(3)?.select()
         }
         _profile_nav_btn.setOnClickListener {
             selectedAccount()
             main_tabs_bottom_nav.getTabAt(4)?.select()
         }*/
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

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, AddPhotoFragment.IMAGE_PICK_CODE)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@LandingActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE/*, Manifest.permission.CAMERA*/),
            PERMISSION_REQUEST_CODE
        )
    }

    fun showAddPhotoDialog() {

        val changeProfile = AddPhotoDialog()
        changeProfile.show(supportFragmentManager, "")
    }

    fun getPermissions() {
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this@LandingActivity,
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
                    UploadWorker.triggerImageUploadingWorker(this)
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

    fun showCameraPicker() {
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this@LandingActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission

                    requestPermissions(permissions, AddPhotoFragment.PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        } else {
            requestPermission()
        }

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val selection: String? = null     //Selection criteria
        val selectionArgs = arrayOf<String>()  //Selection criteria
        val sortOrder: String? = null
    }

    private fun checkPermission(): Boolean {
        return (/*ContextCompat.checkSelfPermission(this@LandingActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && */ContextCompat.checkSelfPermission(
            this@LandingActivity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun getSHA512(input: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(input.toByteArray())

        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)

        // Convert message digest into hex value
        var hashtext: String = no.toString(16)

        // Add preceding 0s to make it 32 bit
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }

        // return the HashText
        return hashtext
    }


    public fun gotoBackUpSettings() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_container) as NavHostFragment
        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_landing)

        graph.startDestination = R.id.backupSettingsFragment
        navHostFragment.navController.graph = graph

    }

    /*override fun onBackPressed() = navManager.onBackPressed()*/

    override fun onBackPressed() {
        onExit()
    }


}