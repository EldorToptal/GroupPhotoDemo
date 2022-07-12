package com.groupphoto.app.presentation.backuphistory.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType
import com.groupphoto.app.presentation.backuphistory.BackUpFilesFragment

class BackUpFilesViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return BackUpFilesFragment.getInstance(getBackUpType(position))
    }

    private fun getBackUpType(position: Int): BackUpFilesType {
        return when (position) {
            0 -> BackUpFilesType.ACTIVE
            1 -> BackUpFilesType.COMPLETED
            2 -> BackUpFilesType.FAILED
            else -> BackUpFilesType.ACTIVE
        }
    }
}