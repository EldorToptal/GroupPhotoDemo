package com.groupphoto.app.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseBottomSheetDialogFragment<T : ViewBinding>(val inflate: Inflate<T>) : BottomSheetDialogFragment() {

    private var _binding: T? = null
    val binding get() = _binding!!
    val bindingSafe get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}