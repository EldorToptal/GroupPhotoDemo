package com.groupphoto.app.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.groupphoto.app.R
import com.groupphoto.app.presentation.landing.BackupOptionsActivity
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.util.AnimationTemplateUtils
import com.groupphoto.app.util.BetterActivityResult
import com.groupphoto.app.util.extensions.SnackbarType
import com.groupphoto.app.util.extensions.showSnackbar
import com.groupphoto.app.data.repository.utils.toPrettyJson
import com.groupphoto.app.databinding.FragmentAuthBinding
import com.groupphoto.app.presentation.MainActivity
import com.groupphoto.app.util.observe
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel
import timber.log.Timber

class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    companion object {
        private const val RC_SIGN_IN = 9001
        const val GoogleSignInTag = "GoogleSignInTag"
    }

    private val authViewModel: AuthViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationTemplateUtils.animateStepByStepVisible(
            arrayOf(
                binding.btnAuthGoogle,
                binding.btnAuthEmail,
                binding.btnAuthApple,
                binding.llAuthInfo
            )
        )

        binding.btnAuthGoogle.setOnClickListener {
            signInWithGoogle()
        }
        binding.btnAuthEmail.setOnClickListener {
            val action =
                AuthFragmentDirections.loginToSignup(
                    authViewModel.authOption.value == AuthViewModel.AuthOption.LOGIN
                )
            findNavController().navigate(action)
        }
        binding.btnAuthApple.setOnClickListener {
            authViewModel.signInWithApple(requireActivity())
        }
        binding.llAuthInfo.setOnClickListener {
            authViewModel.toggleAuthOption()
        }

        binding.textViewTerms.setOnClickListener(::onTermsClicked)
        binding.tvTermsInfo.setOnClickListener(::onTermsClicked)

        observe(authViewModel.authResource, ::firebaseAuthLoaded)
        observe(authViewModel.loading, ::fullScreenLoadingState)
        observe(authViewModel.errorOther, ::showErrorSnackbar)
        observe(authViewModel.authOption, ::authOptionSelection)

    }

    private fun authOptionSelection(authOption: AuthViewModel.AuthOption) {
        when (authOption) {
            AuthViewModel.AuthOption.LOGIN -> {
                binding.tvAuthGoogle.setText(getString(R.string.LoginWithGoogle))
                binding.tvAuthEmail.setText(getString(R.string.LoginWithEmail))
                binding.tvAuthApple.setText(getString(R.string.LoginWithApple))
                binding.tvAuthOption.setText(getString(R.string.Login))
                binding.tvAuthOptionLabel.setText(getString(R.string.AlreadyHaveAccount))
            }
            AuthViewModel.AuthOption.SIGNUP -> {
                binding.tvAuthGoogle.setText(getString(R.string.SignUpGoogle))
                binding.tvAuthEmail.setText(getString(R.string.SignUpEmail))
                binding.tvAuthApple.setText(getString(R.string.SignUpApple))
                binding.tvAuthOption.setText(getString(R.string.SignUp))
                binding.tvAuthOptionLabel.setText(getString(R.string.NoAccount))
            }
        }
    }

    private fun firebaseAuthLoaded(authResult: AuthResult) {
        requireActivity().startActivity(MainActivity.getInstance(requireActivity(), true))
        requireActivity().finish()
    }

    private fun onTermsClicked(view: View) {
        findNavController().navigate(R.id.policyFragment)
    }

    private fun signInWithGoogle() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WebClientId))
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        activityLauncher.launch(signInIntent,
            object : BetterActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        authViewModel.signInWithGoogle(account.idToken ?: "")
                    } catch (e: ApiException) {
                        Timber.tag(GoogleSignInTag).d("Google sign in failed:" + e.toPrettyJson())
                        requireActivity().showSnackbar(
                            binding.root,
                            Snackbar.LENGTH_LONG,
                            SnackbarType.ERROR,
                            CommonStatusCodes.getStatusCodeString(e.statusCode),
                            textColor = R.color.GrayscaleMineShaft,
                            backgroundColor = R.color.NeutralsAquaSpring
                        )
                    }
                }
            })
    }

}

