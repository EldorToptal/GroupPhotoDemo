package com.groupphoto.app.presentation.main.pools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.pawegio.kandroid.e
import com.pawegio.kandroid.startActivity
import kotlinx.android.synthetic.main.activity_invite_contacts.*

import org.koin.androidx.viewmodel.ext.viewModel


class InviteContactActivity : AppCompatActivity() {

    private val viewModel: LandingActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_contacts)


        invite_from_contacts.setOnClickListener {
            startActivity<LocalContactsActivity>()
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