package com.groupphoto.app.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.AuthResult
import com.groupphoto.app.R
import com.groupphoto.app.databinding.FragmentSignupBinding
import com.groupphoto.app.presentation.MainActivity
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.presentation.landing.BackupOptionsActivity
import com.groupphoto.app.presentation.landing.LandingActivity
import com.groupphoto.app.util.AnimationTemplateUtils
import com.groupphoto.app.util.observe
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel


class SignEmailFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModel()
    private val args: SignEmailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationTemplateUtils.animateStepByStepVisible(
            arrayOf(
                binding.tvSignUpTitle,
                binding.etEmail,
                binding.etPassword,
            )
        )
        binding.tvSignUpTitle.setText(
            if (args.isLogin)
                getString(R.string.LoginWithEmail)
            else
                getString(R.string.SignUpEmail)
        )

        binding.textViewTerms.setOnClickListener(::onTermsClicked)
        binding.tvTermsInfo.setOnClickListener(::onTermsClicked)

        binding.btnNext.setOnClickListener {
            if (binding.etPassword.hasError() || binding.etEmail.hasError())
                return@setOnClickListener
            if (args.isLogin)
                authViewModel.signInWithEmail(
                    binding.etEmail.getText(),
                    binding.etPassword.getText()
                )
            else
                authViewModel.signUpWithEmail(
                    binding.etEmail.getText(),
                    binding.etPassword.getText()
                )

        }
        observe(authViewModel.authResource, ::firebaseAuthLoaded)
        observe(authViewModel.loading, ::fullScreenLoadingState)
        observe(authViewModel.errorOther, ::showErrorSnackbar)
        binding.tbSignUp.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun firebaseAuthLoaded(authResult: AuthResult) {
        requireActivity().startActivity(MainActivity.getInstance(requireActivity(), true))
        requireActivity().finish()
    }

    private fun onTermsClicked(view: View) {
        findNavController().navigate(R.id.policyFragment)
    }

}