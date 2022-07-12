package com.groupphoto.app.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groupphoto.app.R
import com.groupphoto.app.databinding.DialogConfirmLoginBinding
import com.groupphoto.app.presentation.base.BaseBottomSheetDialogFragment


class ConfirmLoginDialog :
    BaseBottomSheetDialogFragment<DialogConfirmLoginBinding>(DialogConfirmLoginBinding::inflate) {

    var strEmail: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_confirm_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView24.text = strEmail
    }
}