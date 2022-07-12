package com.groupphoto.app.presentation.main.pools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ActivityPoolSettingsBinding
import com.groupphoto.app.presentation.base.BaseActivity
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.pawegio.kandroid.e
import com.pawegio.kandroid.startActivity
import kotlinx.android.synthetic.main.activity_pool_settings.*

import org.koin.androidx.viewmodel.ext.viewModel


class PoolSettingsActivity : BaseActivity<ActivityPoolSettingsBinding>(ActivityPoolSettingsBinding::inflate) {

    private val viewModel: LandingActivityViewModel by viewModel()

    override fun init(savedInstanceState: Bundle?) {

        binding.clColor.setOnClickListener {
            startActivity<PoolColorPickerActivity>()
        }
        binding.btnSavePool.setOnClickListener {
            finish()
        }
        binding.btnArchivePool.setOnClickListener {
            val archivePoolDialog = ConfirmArchivePoolDialog()
            archivePoolDialog.isCancelable = true
            archivePoolDialog.show(supportFragmentManager, "")
        }
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LandingActivityEvent.TabSwitchRequested -> e("Landing")
            }
        })
    }
}