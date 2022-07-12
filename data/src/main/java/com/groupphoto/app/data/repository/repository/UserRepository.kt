package com.groupphoto.app.data.repository.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.groupphoto.app.data.repository.local.pref.UserPref
import com.groupphoto.app.data.repository.pojo.UserCollection
import com.groupphoto.app.data.repository.pojo.UserInfoResponse
import com.groupphoto.app.data.repository.utils.network.ErrorResponse
import com.groupphoto.app.data.repository.utils.network.Resource
import com.groupphoto.app.data.repository.utils.toPrettyJson
import timber.log.Timber
import java.lang.Exception

interface UserRepository {
    fun getUserInfo(
        userResource: (Resource<UserInfoResponse>) -> Unit
    )
}

class UserRepositoryImpl(private val firestore: FirebaseFirestore) : UserRepository {

    override fun getUserInfo(
        userResource: (Resource<UserInfoResponse>) -> Unit
    ) {
        val userInfo = firestore.collection(UserCollection.USERS.collectionName.lowercase())
            .document(UserPref.uid)
        val userServicePlans =
            firestore.collection(UserCollection.USERS_SERVICE_PLANS.collectionName.lowercase())
                .document(UserPref.uid)
        getUserData(userInfo, UserCollection.USERS, userResource)
        getUserData(userServicePlans, UserCollection.USERS_SERVICE_PLANS, userResource)

    }

    private fun getUserData(
        userInfo: DocumentReference,
        collectionType: UserCollection,
        userResource: (Resource<UserInfoResponse>) -> Unit
    ) {
        userInfo.addSnapshotListener { documentSnapshot, error ->
            if (error != null) {
                Timber.d("Document ${error.toPrettyJson()}")
                userResource.invoke(Resource.Error(ErrorResponse(exception = Exception(error.message))))
            }
            Timber.d("Document11 ${documentSnapshot?.data?.toPrettyJson()}")
            if (documentSnapshot != null
                && documentSnapshot.exists()
            ) {
                Timber.d("Document ${documentSnapshot.data?.toPrettyJson()}")
                val userInfoPlans =
                    documentSnapshot.toObject(UserInfoResponse::class.java) as UserInfoResponse
                userInfoPlans.isUserInfoRequest = collectionType == UserCollection.USERS
                userResource.invoke(Resource.Success<UserInfoResponse>(userInfoPlans))
            } else {
                Timber.d("Document does not exist")
                userResource.invoke(Resource.Error(ErrorResponse(exception = Exception("Document Snapshot does not exist!!!"))))
            }
        }
    }
}