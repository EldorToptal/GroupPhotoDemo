package com.groupphoto.app.presentation.main.pools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.pawegio.kandroid.e
import com.pawegio.kandroid.startActivity
import kotlinx.android.synthetic.main.include_new_pool.*

import org.koin.androidx.viewmodel.ext.viewModel


class NewPoolActivity : AppCompatActivity() {

    private val viewModel: LandingActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pool)

        new_pool_next.setOnClickListener {
            startActivity<InviteContactActivity>()
        }
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LandingActivityEvent.TabSwitchRequested -> e("Landing")
            }
        })

    }






    companion object {
        private const val TAG = "Gallery Activity "
    }
}