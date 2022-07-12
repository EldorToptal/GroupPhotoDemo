package com.groupphoto.app.presentation.main.pools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.pawegio.kandroid.e
import kotlinx.android.synthetic.main.include_bar_view_image.*

import org.koin.androidx.viewmodel.ext.viewModel


class ViewImageActivity : AppCompatActivity() {

    private val viewModel: LandingActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        imageView19.setOnClickListener {
            onBackPressed()
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