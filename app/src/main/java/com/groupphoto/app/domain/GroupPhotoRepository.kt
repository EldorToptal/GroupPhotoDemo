package com.groupphoto.app.domain

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.groupphoto.app.data.remote.GroupPhotoService
import com.groupphoto.app.data.remote.model.GalleryAssetItem
import com.groupphoto.app.data.remote.model.PoolItem
import com.groupphoto.app.data.remote.model.User
import com.pawegio.kandroid.e

interface GroupPhotoRepository {
    var user: User?
    fun firebaseSignInWithGoogle( idToken: String?, onAuthenticated: (User) -> Unit)
    fun firebaseLogInWithGoogle( idToken: String?, onAuthenticated: (User) -> Unit)
    fun firebaseSignInWithFacebook( idToken: String?, onAuthenticated: (User) -> Unit)
    fun firebaseSignUpWithEmail( email: String, onAuthenticated: (User) -> Unit)
    fun firebaseLoginWithEmailPassword( email: String, password: String,  onAuthenticated: (User) -> Unit)
    fun firebaseSignUpWithEmailPassword( email: String, password: String,  onAuthenticated: (User) -> Unit)
    fun firebaseSignInWithEmail( email: String,intent: Intent, onAuthenticated: (User) -> Unit)
    fun firebaseSignInWithApple(idToken: String?, ac : Activity, onAuthenticated: (User) -> Unit)
    fun getPoolsList(pools: (MutableList<PoolItem>) -> Unit)
    fun getGalleryLow(assets: (MutableList<GalleryAssetItem>) -> Unit)
}
class GroupPhotoRepositoryImpl( private val groupPhotoService: GroupPhotoService ) : GroupPhotoRepository {

    override var user: User? = null

    override fun firebaseSignInWithGoogle(idToken: String?, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignInWithGoogle(idToken,onAuthenticated)
    }
    override fun firebaseLogInWithGoogle(idToken: String?, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignInWithGoogle(idToken,onAuthenticated)
    }
    override fun firebaseSignInWithFacebook(idToken: String?, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignInWithFacebook(idToken,onAuthenticated)
    }
    override fun firebaseSignUpWithEmail(email: String, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignUpWithEmail(email,onAuthenticated)
    }
    override fun firebaseLoginWithEmailPassword(email: String,password: String, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseLoginWithEmailPassword(email,password, onAuthenticated)
    }
    override fun firebaseSignUpWithEmailPassword(email: String,password: String, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignUpWithEmailPassword(email,password, onAuthenticated)
    }
    override fun firebaseSignInWithEmail(email: String,intent: Intent, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignInWithEmail(email,intent,onAuthenticated)
    }

    override fun firebaseSignInWithApple(idToken: String?, ac : Activity, onAuthenticated: (User) -> Unit) {
        groupPhotoService.firebaseSignInWithApple(idToken, onAuthenticated, ac)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun getPoolsList(pools: (MutableList<PoolItem>) -> Unit) {
        e("com.groupphoto.app.data.repository get pools list")
        groupPhotoService.getPoolsList(pools)
    }
    override fun getGalleryLow(assets: (MutableList<GalleryAssetItem>) -> Unit) {
        groupPhotoService.getGalleryLow(assets)
    }
    companion object {
        private const val TAG = "GroupPhotoRepository"
    }
}

