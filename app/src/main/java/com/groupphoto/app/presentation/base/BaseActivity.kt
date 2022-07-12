package com.groupphoto.app.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding>(val bindingFactory: (LayoutInflater) -> T) :
    AppCompatActivity() {

    private var isEndFlag // double click back button to kill the app
            = false
    lateinit var binding: T
    val bindingSafe: T?
        get() = if (isBindingInitialized()) binding else null

    open fun isBindingInitialized(): Boolean = ::binding.isInitialized
    open fun isBindingNotInitialized(): Boolean = !(::binding.isInitialized)

    abstract fun init(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        init(savedInstanceState)

    }


    fun onExit() {
//        if (!isEndFlag) {
//            Toast.makeText(
//                this, getString(R.string.BackButtonMessage),
//                Toast.LENGTH_SHORT
//            ).show()
//            isEndFlag = true
//        } else {
//            finish()
//        }
    }
}