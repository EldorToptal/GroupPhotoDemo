package com.groupphoto.app.presentation.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.local.pref.UserPref
import com.groupphoto.app.data.repository.pojo.UserInfoResponse
import com.groupphoto.app.data.repository.local.dao.BackupRoomDatabase
import com.groupphoto.app.databinding.FragmentProfileBinding
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.presentation.auth.AuthActivity
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.util.Constants
import com.groupphoto.app.worker.UploadWorker
import com.groupphoto.app.util.extensions.decimalPlaces
import com.groupphoto.app.util.extensions.loadCircularImage
import com.groupphoto.app.util.extensions.manageVisibility
import com.groupphoto.app.util.observe
import com.pawegio.kandroid.e
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import org.koin.androidx.viewmodel.ext.viewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate),
    View.OnClickListener {

    var savedEntity: List<BackupEntity> = ArrayList()

    private lateinit var functions: FirebaseFunctions
    lateinit var paymentsBehavior: BottomSheetBehavior<FrameLayout>
    private val viewModel: ProfileViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarColor(R.color.white, false)
        binding.tvEmail.text = UserPref.email
        binding.tvUserName.text = UserPref.displayName
        binding.tvAvatarLetter.text =
            if (UserPref.displayName.length > 0)
                UserPref.displayName.substring(0, 1)
            else
                ""

        val database =
            BackupRoomDatabase.getDatabase(requireContext()).backupDao()

//        savedEntity = database.getAllSavedData()

        binding.pbUploadingImage.progress = (getAllSize().decimalPlaces(0).toInt() / 1024)

        val currentSize: String = getAllSize().decimalPlaces(2).toFloat().toString()
        val maxSize: String = (binding.pbUploadingImage.max / 1024).toString()

        binding.tvUsedStorage.text = "${currentSize}MB of ${maxSize}GB"

        binding.menuLogout.setOnClickListener(this)
        binding.menuBackupSettings.setOnClickListener(this)
        binding.menuBackupHistory.setOnClickListener(this)

        functions = Firebase.functions

        paymentsBehavior =
            BottomSheetBehavior.from(binding.fSubscription.bshSubscriptions).apply { }
        paymentsBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })
        paymentsBehavior.skipCollapsed = true
        binding.tvUpgrade.setOnClickListener {
            viewModel.getUserInfo()
//            paymentsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.fSubscription.btnPay.setOnClickListener {
        }
        viewModel.getUserInfo()
        observe(viewModel.userResource, ::onUserInfoLoaded)
        observe(viewModel.errorOther, ::showErrorSnackbar)
        observe(viewModel.loading, ::onLoadingProfileInfo)
    }

    private fun onLoadingProfileInfo(isLoading: Boolean) {
        binding.pbProfile.manageVisibility(isLoading)
    }

    private fun onUserInfoLoaded(userInfoResponse: UserInfoResponse) {
        if (userInfoResponse.isUserInfoRequest) {
            binding.tvEmail.text = userInfoResponse.email
            binding.tvUserName.text = userInfoResponse.fullName
            binding.tvAvatarLetter.text =
                if (userInfoResponse.fullName?.length ?: 0 > 0)
                    userInfoResponse.fullName?.substring(0, 1)
                else
                    ""
            binding.ivAvatar.loadCircularImage(userInfoResponse.avatarURL)
        } else {
            binding.tvUsedStorage.text =
                ("${userInfoResponse.usedBytes} of ${userInfoResponse.getCapacityForSize()}")
            binding.pbUploadingImage.progress = userInfoResponse.getProgressOfUsedBytes()
            binding.tvPlanStatus.text = ("${userInfoResponse.type?.uppercase(Locale.ROOT)} Plan")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.menuLogout -> gotoLogOut()
            R.id.menuBackupSettings -> {
                findNavController().navigate(R.id.backupSettingsFragment)
            }
            R.id.menuBackupHistory -> {
                UploadWorker.triggerImageUploadingWorker(requireContext().applicationContext)
                findNavController().navigate(R.id.backupHistoryFragment)
            }
        }
    }

    private fun gotoLogOut() {

//        Prefs.clear()
        SharedPref.clear()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.putExtra("loginFragment", true)
        activity?.startActivity(intent)
        activity?.finish()

    }

    private fun getAllSize(): (Double) {
        var size: Double = 0.0 //MB

        for (entity in savedEntity) {
            size += entity.fileSize
        }
        return size
    }

    private fun onGooglePayReady(isReady: Boolean) {
        binding.fSubscription.btnPay.isEnabled = isReady
    }

    private fun onGooglePayResult(
        result: GooglePayLauncher.Result
    ) {
        when (result) {
            is GooglePayLauncher.Result.Completed -> {
                // Payment details successfully captured.
                // Send the paymentMethodId to your server to finalize payment.
                val paymentMethodId = result
            }
            GooglePayLauncher.Result.Canceled -> {
                // User cancelled the operation
            }
            is GooglePayLauncher.Result.Failed -> {
                // Operation failed; inspect `result.error` for the exception
            }
        }
    }
}
