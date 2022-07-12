package com.groupphoto.app.presentation.auth.signup

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.local.pref.SharedPref.loginEmail
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.databinding.FragmentRegisterWithEmailPasswordBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.presentation.landing.BackupOptionsActivity
import com.groupphoto.app.presentation.viewmodels.RegisterWithEmailPasswordEvent
import com.groupphoto.app.presentation.viewmodels.RegisterWithEmailPasswordViewModel
import com.groupphoto.app.util.Constants
import com.groupphoto.app.util.extensions.isValidEmail
import com.pawegio.kandroid.toast
import com.pixplicity.easyprefs.library.Prefs
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel


class SignupWithEmailPasswordFragment :
    BaseFragment<FragmentRegisterWithEmailPasswordBinding>(FragmentRegisterWithEmailPasswordBinding::inflate) {

    private val viewModel: RegisterWithEmailPasswordViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is RegisterWithEmailPasswordEvent.OnRegisterButtonClicked -> onRegisterClicked(event.result)
            }
        })
        binding.loginEmailPasswordBtn.setOnClickListener {
            if (binding.registerEmailField.text.toString().isValidEmail()) {
                if (binding.registerPassword.text.toString().count() > 8) {
                    if (binding.registerPassword2.text.toString() == binding.registerPassword2.text.toString()) {
                        loader.show()
                        loginEmail = binding.registerEmailField.text.toString()
                        binding.registerEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
                        binding.loginValidationMessage.visibility = View.GONE
                        viewModel.registerButtonClicked(
                            binding.registerEmailField.text.toString(),
                            binding.registerPassword.text.toString(),
                            requireActivity().intent
                        )
                    } else {
                        //Password Confirmation Failed

                        toast("Password confirmation did not match.")
                    }
                } else {
                    //Password count failed
                    toast("Password must be at least 9 characters.")
                }


            } else {
                val shape = ShapeDrawable(RectShape())
                shape.paint.color = Color.RED
                shape.paint.style = Paint.Style.STROKE
                shape.paint.strokeWidth = 5f
                binding.registerEmailField.background = shape
                binding.loginValidationMessage.text = "Email Format Invalid"
                binding.loginValidationMessage.visibility = View.VISIBLE
            }

        }
        binding.loginEmailPasswordBackBtn.setOnClickListener {
            findNavController().navigate(R.id.signEmailFragment)
        }
    }

    private fun onRegisterClicked(user: User?) {
        loader.hide()
        if (user!!.isAuthenticated) {

            Prefs.putString(Constants.KEY_PWD, binding.registerPassword.text.toString())
            Prefs.putString(Constants.KEY_LOGIN_TYPE, "emailPass")
            Prefs.putString(Constants.KEY_EMAIL, binding.registerEmailField.text.toString())
            SharedPref.isUserLoggedIn = true
            Constants.g_user = user
            SharedPref.userId = user!!.uid!!
            requireActivity().startActivity<BackupOptionsActivity>()

            requireActivity().finish()
        } else {
            Toast.makeText(context, user.errorMsg, Toast.LENGTH_LONG)
                .show()
        }

    }
}