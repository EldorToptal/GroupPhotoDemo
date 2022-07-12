package com.groupphoto.app.presentation.main.profile

import com.groupphoto.app.data.repository.pojo.UserCollection
import com.groupphoto.app.data.repository.pojo.UserInfoResponse
import com.groupphoto.app.data.repository.repository.UserRepository
import com.groupphoto.app.data.repository.utils.network.Resource

interface ProfileInteractor {
    fun getUserInfo(userResource: (Resource<UserInfoResponse>) -> Unit)
}

class ProfileInteractorImpl(val userRepository: UserRepository) : ProfileInteractor {
    override fun getUserInfo(
        userResource: (Resource<UserInfoResponse>) -> Unit
    ) {
        userRepository.getUserInfo( userResource)
    }

}