package com.groupphoto.app.presentation.auth.policy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.groupphoto.app.R
import com.groupphoto.app.databinding.FragmentPolicyBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.util.extensions.onBackPressedCustomAction

class PolicyFragment : BaseFragment<FragmentPolicyBinding>(FragmentPolicyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(getString(R.string.TermsOfServiceUrl))
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)

        binding.tbPolicy.setNavigationOnClickListener{
            handleBackNavigation()
        }
        onBackPressedCustomAction {
            handleBackNavigation()
        }
    }

    private fun handleBackNavigation(){
        if(binding.webView.canGoBack())
            binding.webView.goBack()
        else
            findNavController().popBackStack()
    }

}