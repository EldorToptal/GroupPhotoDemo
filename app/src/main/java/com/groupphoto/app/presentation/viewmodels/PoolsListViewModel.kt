package com.groupphoto.app.presentation.viewmodels

import com.groupphoto.app.data.remote.model.PoolItem
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class PoolsListViewModel(
    private val groupPhotoRepository: GroupPhotoRepository
) : RxViewModel() {
    private val _event = SingleLiveEvent<PoolsListEvent>()
    val event: SingleLiveEvent<PoolsListEvent> get() = _event
    fun getPoolsList() {
        groupPhotoRepository.getPoolsList(pools = { user ->
            _event.value =
                PoolsListEvent.OnGetPoolsList(user)
        } )
    }
}

sealed class PoolsListEvent {
    data class OnGetPoolsList(val pools: MutableList<PoolItem>?) : PoolsListEvent()
}
