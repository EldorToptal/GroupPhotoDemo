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
import com.groupphoto.app.data.repository.local.pref.SharedPref.loginEmail
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.databinding.FragmentLoginEmailBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.presentation.viewmodels.LogInWithEmailViewModel
import com.groupphoto.app.presentation.viewmodels.LoginWithEmailEvent
import com.groupphoto.app.util.extensions.isValidEmail
import org.koin.androidx.viewmodel.ext.viewModel


class LoginWithEmailFragment :
    BaseFragment<FragmentLoginEmailBinding>(FragmentLoginEmailBinding::inflate) {

    private val viewModel: LogInWithEmailViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LoginWithEmailEvent.OnLoginButtonClicked -> onLoginButtonClicked(event.result)
            }
        })
        binding.loginBtn.setOnClickListener {
            if (binding.loginEmailField.text.toString().isValidEmail()) {
                loader.show()
                loginEmail = binding.loginEmailField.text.toString()
                binding.loginEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
                binding.loginValidationMessage.visibility = View.GONE
                viewModel.loginButtonClicked(
                    binding.loginEmailField.text.toString(),
                    requireActivity().intent
                )

            } else {
                val shape = ShapeDrawable(RectShape())
                shape.paint.color = Color.RED
                shape.paint.style = Paint.Style.STROKE
                shape.paint.strokeWidth = 5f
                binding.loginEmailField.background = shape
                binding.loginValidationMessage.visibility = View.VISIBLE
            }

        }
        binding.loginEmailBackBtn.setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
    }

    private fun onLoginButtonClicked(user: User?) {
        loader.hide()
        if (user!!.isAuthenticated) {
            val changeProfile = ConfirmLoginDialog()
            changeProfile.strEmail = binding.loginEmailField.text.toString()
            changeProfile.show(childFragmentManager, "")
//            Toast.makeText(context, "Email verification sent to ${user.email}. Please verify your log in.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, user.errorMsg, Toast.LENGTH_LONG)
                .show()
        }

    }
}