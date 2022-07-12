package com.groupphoto.app.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.groupphoto.app.databinding.ActivityAuthBinding
import com.groupphoto.app.presentation.base.BaseActivity

class AuthActivity : BaseActivity<ActivityAuthBinding>(ActivityAuthBinding::inflate) {

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, AuthActivity::class.java))
        }
    }
    override fun init(savedInstanceState: Bundle?) {
    }


}