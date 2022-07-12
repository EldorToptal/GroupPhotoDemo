package com.groupphoto.app.presentation.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.groupphoto.app.R
import kotlinx.android.synthetic.main.fragment_splash.*
import com.groupphoto.app.util.BetterActivityResult

import android.content.Intent
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.groupphoto.app.data.repository.utils.toPrettyJson
import com.groupphoto.app.util.extensions.SnackbarType
import com.groupphoto.app.util.extensions.onBackPressedCustomAction
import com.groupphoto.app.util.extensions.showSnackbar
import timber.log.Timber


typealias Inflate<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    protected val activityLauncher: BetterActivityResult<Intent, ActivityResult> =
        BetterActivityResult.registerActivityForResult(this)
    private var _binding: VB? = null
    protected var bindingSafe = _binding
    protected val binding: VB
        get() = _binding!!

    protected var hasToolbar = false

    protected val loader by lazy {
        class DialogDismissLifecycleObserver(private var dialog: AlertDialog?) :
            LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                dialog?.dismiss()
                dialog = null
            }
        }
        AlertDialog.Builder(requireContext()).create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setView(
                layoutInflater.inflate(
                    R.layout.dialog_loader,
                    activity_auth
                )
            )
            lifecycle.addObserver(DialogDismissLifecycleObserver(this))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        if (hasToolbar)
            toolbarNavigation()
        return binding.root
    }

    /**
     * @param colorId id of color
     * @param isStatusBarFontDark Light or Dark color
     */
    fun updateStatusBarColor(
        @ColorRes colorId: Int,
        isStatusBarFontDark: Boolean = true,
        isTranslucent: Boolean = false
    ) {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), colorId)
            setSystemBarTheme(isStatusBarFontDark)
        } else {
            window.statusBarColor = ContextCompat.getColor(requireContext(), colorId)
        }
    }

    /** Changes the System Bar Theme.
     * @param isStatusBarFontDark status of darkness
     * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setSystemBarTheme(isStatusBarFontDark: Boolean) {
        // Fetch the current flags.
        val lFlags = requireActivity().window.decorView.systemUiVisibility
        // Update the SystemUiVisibility depending on whether we want a Light or Dark theme.
        requireActivity().window.decorView.systemUiVisibility =
            if (isStatusBarFontDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun hideStatusBar() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun fullScreenLoadingState(isLoading: Boolean) {
        if (isLoading) {
            loader.show()
        } else {
            loader.hide()
        }
    }

    open fun showMessageSnackbar(message: String) {
        requireActivity().showSnackbar(
            view as ViewGroup,
            Snackbar.LENGTH_LONG,
            SnackbarType.DEFAULT,
            message,
            textColor = R.color.GrayscaleMineShaft
        )
    }

    open fun showErrorSnackbar(message: String) {
        requireActivity().showSnackbar(
            view as ViewGroup,
            Snackbar.LENGTH_LONG,
            SnackbarType.ERROR,
            message,
            textColor = R.color.white
        )
    }

    override fun onStop() {
        loader.hide()
        super.onStop()
    }

    open fun toolbarNavigation() {
        binding.root.findViewWithTag<Toolbar>("toolbar").setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}