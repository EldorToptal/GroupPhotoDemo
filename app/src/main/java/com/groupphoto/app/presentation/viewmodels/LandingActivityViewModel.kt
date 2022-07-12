package com.groupphoto.app.presentation.viewmodels

import android.os.Bundle
import com.groupphoto.app.data.repository.local.dao.BackupRoomRepository
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class LandingActivityViewModel(
    private val backupRoomRepository: BackupRoomRepository
) : RxViewModel() {
    private val _event = SingleLiveEvent<LandingActivityEvent>()
    val event: SingleLiveEvent<LandingActivityEvent> get() = _event
    fun requestTabSwitch(
        position: Int,
        toDestination: Int? = null,
        inGraph: Int? = null,
        withExceptionDestinations: List<Int>? = null,
        arguments: Bundle? = null
    ) {
        _event.value = LandingActivityEvent.TabSwitchRequested(
            position,
            toDestination,
            inGraph,
            withExceptionDestinations,
            arguments
        )
    }

    fun backupFile(backup: BackupEntity) {
        backupRoomRepository.insert(backup)
        _event.value =
            LandingActivityEvent.OnInsertItem

    }

}

sealed class LandingActivityEvent {
    data class TabSwitchRequested(
        val tabPosition: Int,
        val toDestination: Int?,
        val inGraph: Int?,
        val withExceptionDestinations: List<Int>?,
        val arguments: Bundle? = null
    ) : LandingActivityEvent()
    object OnInsertItem : LandingActivityEvent()
}
