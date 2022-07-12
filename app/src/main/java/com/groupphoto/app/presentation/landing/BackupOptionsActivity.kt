package com.groupphoto.app.presentation.landing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.SharedPref.backupOption
import com.groupphoto.app.data.repository.local.pref.SharedPref.loginEmail
import com.groupphoto.app.databinding.ActivityBackupOptionsBinding
import com.groupphoto.app.presentation.base.BaseActivity
import com.groupphoto.app.presentation.WebViewActivity
import com.pawegio.kandroid.e
import org.jetbrains.anko.startActivity


class BackupOptionsActivity :
    BaseActivity<ActivityBackupOptionsBinding>(ActivityBackupOptionsBinding::inflate) {

    override fun init(savedInstanceState: Bundle?) {


    }

}