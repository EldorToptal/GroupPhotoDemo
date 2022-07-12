package com.groupphoto.app.presentation.viewmodels

import com.groupphoto.app.data.remote.model.GalleryAssetItem
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class GalleryViewModel(
    private val groupPhotoRepository: GroupPhotoRepository
) : RxViewModel() {
    private val _event = SingleLiveEvent<GalleryEvent>()
    val event: SingleLiveEvent<GalleryEvent> get() = _event
    fun getImageList() {
        groupPhotoRepository.getGalleryLow (assets = { assets ->
            _event.value =
                GalleryEvent.OnGetAssetsList(assets)
        } )
    }
}

sealed class GalleryEvent {
    data class OnGetAssetsList(val assets: MutableList<GalleryAssetItem>?) : GalleryEvent()
}
