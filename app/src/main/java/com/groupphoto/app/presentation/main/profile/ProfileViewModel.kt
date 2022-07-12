package com.groupphoto.app.presentation.main.profile

import com.google.firebase.auth.UserInfo
import com.groupphoto.app.data.repository.pojo.UserCollection
import com.groupphoto.app.data.repository.pojo.UserInfoResponse
import com.groupphoto.app.data.repository.utils.network.LiveEvent
import com.groupphoto.app.data.repository.utils.network.Resource
import com.groupphoto.app.presentation.base.BaseViewModel
import java.lang.Exception

class ProfileViewModel(private val interactor: ProfileInteractor) : BaseViewModel() {
    val userResource: LiveEvent<UserInfoResponse> = LiveEvent()

    fun getUserInfo() {
        loading.postValue(true)
        try {
            interactor.getUserInfo(::onUserInfoLoaded)
        } catch (e: Exception) {
            errorProcess(e)
        }
    }

    fun onUserInfoLoaded(resource: Resource<UserInfoResponse>) {
        loading.postValue(false)
        if (resource is Resource.Success) {
            userResource.value = resource.data
        }
        if (resource is Resource.Error) {
            errorProcess(resource.errorResponse.exception)
        }
    }

}