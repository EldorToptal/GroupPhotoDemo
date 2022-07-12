package com.groupphoto.app.presentation.auth.login

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
import com.groupphoto.app.databinding.FragmentLoginEmailPasswordBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.presentation.landing.LandingActivity
import com.groupphoto.app.presentation.viewmodels.LogInWithEmailPasswordViewModel
import com.groupphoto.app.presentation.viewmodels.LoginWithEmailPasswordEvent
import com.groupphoto.app.util.Constants.g_user
import com.groupphoto.app.util.extensions.isValidEmail
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel


class LoginWithEmailPasswordFragment : BaseFragment<FragmentLoginEmailPasswordBinding>(FragmentLoginEmailPasswordBinding::inflate) {

    private val viewModel: LogInWithEmailPasswordViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LoginWithEmailPasswordEvent.OnLoginButtonClicked -> onLoginButtonClicked(event.result)
            }
        })
        binding.loginEmailPasswordBtn.setOnClickListener {
            if (binding.loginEmailField.text.toString().isValidEmail()) {
                loader.show()
                loginEmail = binding.loginEmailField.text.toString()
                binding.loginEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
                binding.loginValidationMessage.visibility = View.GONE
                viewModel.loginButtonClicked(binding.loginEmailField.text.toString(),binding.loginPassword.text.toString(), requireActivity().intent)

            } else {
                val shape = ShapeDrawable(RectShape())
                shape.paint.color = Color.RED
                shape.paint.style = Paint.Style.STROKE
                shape.paint.strokeWidth = 5f
                binding.loginEmailField.background = shape
                binding.loginValidationMessage.visibility = View.VISIBLE
            }

        }
        binding.loginEmailPasswordBackBtn.setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
    }

    private fun onLoginButtonClicked(user: User?) {
        loader.hide()
        if (user!!.isAuthenticated) {

            g_user = user
//
//            Prefs.putString(Constants.KEY_PWD, binding.loginPassword.text.toString())
//            Prefs.putString(Constants.KEY_LOGIN_TYPE, "emailPass")
//            Prefs.putString(Constants.KEY_EMAIL, binding.loginEmailField.text.toString())
            user.email?.let {
                Toast.makeText(context, "Welcome ${user!!.email?.split("@")!![0]} ! ", Toast.LENGTH_LONG).show()
            }
            SharedPref.userId = user!!.uid!!
            SharedPref.isUserLoggedIn = true
            requireActivity().startActivity<LandingActivity>()
            requireActivity().finish()
        } else {
            Toast.makeText(context, user.errorMsg, Toast.LENGTH_LONG)
                .show()
        }

    }
}