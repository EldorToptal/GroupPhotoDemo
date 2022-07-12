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
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.databinding.FragmentSignupEmailBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.presentation.landing.LandingActivity
import com.groupphoto.app.presentation.viewmodels.SignUpWithEmailEvent
import com.groupphoto.app.presentation.viewmodels.SignUpWithEmailViewModel
import com.groupphoto.app.util.extensions.isValidEmail
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel

class SignupWithEmailFragment :
    BaseFragment<FragmentSignupEmailBinding>(FragmentSignupEmailBinding::inflate) {

    private val viewModel: SignUpWithEmailViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shape = ShapeDrawable(RectShape())
        shape.paint.color = Color.RED
        shape.paint.style = Paint.Style.STROKE
        shape.paint.strokeWidth = 5f
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is SignUpWithEmailEvent.OnSignUpButtonClicked -> onSignupButtonClicked(event.result)
            }
        })

        binding.signupEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
        binding.btnSignUp.setOnClickListener {

            if (binding.signupEmailField.text.toString().isValidEmail()) {
                if (binding.signupPhoneField.text.toString().count() in 7..19) {
                    binding.signupPhoneField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
                    binding.signupNumberValidationMessage.visibility = View.GONE
                    binding.signupEmailField.setBackgroundResource(R.drawable.bg_border_gray_4dp)
                    binding.signupEmailValidationMessage.visibility = View.GONE
                    loader.show()
                    viewModel.signupButtonClicked(
                        binding.signupEmailField.text.toString(),
                        requireActivity().intent
                    )
                } else {
                    binding.signupPhoneField.background = shape
                    binding.signupNumberValidationMessage.visibility = View.VISIBLE
                }
            } else {
                binding.signupEmailField.background = shape
                binding.signupEmailValidationMessage.visibility = View.VISIBLE
            }


        }
        binding.signupEmailBackBtn.setOnClickListener {
            findNavController().navigate(R.id.signEmailFragment)
        }
    }

    private fun onSignupButtonClicked(user: User?) {
        loader.hide()
        if (user!!.isAuthenticated) {
            if (user.isNew) {
                Toast.makeText(context, "Welcome ${user.name}", Toast.LENGTH_LONG).show()
                requireActivity().startActivity<LandingActivity>()
                requireActivity().finish()
            } else {
                Toast.makeText(context, user.errorMsg, Toast.LENGTH_LONG).show()

            }
        } else {
            Toast.makeText(context, user.errorMsg, Toast.LENGTH_LONG)
                .show()
        }
    }
}