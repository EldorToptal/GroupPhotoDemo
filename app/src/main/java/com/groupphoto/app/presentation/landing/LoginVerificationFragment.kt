package com.groupphoto.app.presentation.landing

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.groupphoto.app.R

class LoginVerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_landing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firebase.dynamicLinks
            .getDynamicLink(requireActivity().intent)
            .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.e(TAG, "email link $deepLink")
                }


            }
            .addOnFailureListener(requireActivity()) { e -> Log.e(TAG, "getDynamicLink:onFailure", e) }
    }
    companion object {
        private const val TAG = "Login Verification"
        private const val RC_SIGN_IN = 9001
    }
}