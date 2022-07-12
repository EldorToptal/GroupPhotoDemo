package com.groupphoto.app.data.remote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.LoginType
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.local.pref.SharedPref.context
import com.groupphoto.app.data.repository.local.pref.SharedPref.userId
import com.groupphoto.app.data.remote.model.GalleryAssetItem
import com.groupphoto.app.data.remote.model.PoolItem
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.presentation.auth.AuthFragment
import com.groupphoto.app.data.repository.utils.toPrettyJson
import com.pawegio.kandroid.e
import timber.log.Timber
import java.util.concurrent.TimeUnit


class GroupPhotoService {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fireStoreDb = Firebase.firestore

    private val actionCodeSettings = actionCodeSettings {
        url =
            context.getString(R.string.FirebaseBaseUrl) //"https://group-photo-prod.firebaseapp.com/"
        handleCodeInApp = true
        setAndroidPackageName(
            /*"com.group.photo",*/
            context.getString(R.string.PackageName),
            true,
            "21"
        )
    }

    //com.groupphoto.app
    private fun buildActionCodeSettings() {
        val actionCodeSettings = actionCodeSettings {
            url =
                context.getString(R.string.FirebaseBaseUrl) //"https://group-photo-prod.firebaseapp.com/"
            handleCodeInApp = true
            setAndroidPackageName(
                /*"com.group.photo",*/
                context.getString(R.string.PackageName),
                true,
                "21"
            )
        }
    }

    fun firebaseSignInWithGoogle(
        idToken: String?,
        onAuthenticated: (User) -> Unit
    ) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    Timber.tag(AuthFragment.GoogleSignInTag).d("isSuccessful" )
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        Log.e(TAG, "is authenticated")
                        Timber.tag(AuthFragment.GoogleSignInTag).d("authenticated: ${firebaseUser.toPrettyJson()}" )
                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        user.isAuthenticated = true

                        firebaseUser.getIdToken(true)
                            .addOnSuccessListener { myToken ->
                                Log.d(
                                    TAG,
                                    "onSuccess: myToken = ${myToken.token}"
                                )
                                SharedPref.idToken = myToken.token!!.toString()
                            }
                            .addOnFailureListener { failureToken ->
                                Log.d("TokenFailed", failureToken.toString())
                            }

                        onAuthenticated(user)
                    } else {
                        //not registered
                        Timber.tag(AuthFragment.GoogleSignInTag).d("Error: ${ authTask.exception!!.message}" )
                        onAuthenticated(User("", "", "", false, authTask.exception!!.message))

                    }
                } else {
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                    Timber.tag(AuthFragment.GoogleSignInTag).d("Error: ${ authTask.exception!!.message}" )

                }

            }
    }

    fun firebaseSignInWithApple(
        idToken: String?,
        onAuthenticated: (User) -> Unit, ac: Activity
    ) {
        Timber.d("checkPending:onSuccess:$idToken")
        val provider = OAuthProvider.newBuilder("apple.com")
        val pending = firebaseAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                Timber.d("checkPending:onSuccess:" + authResult.user)

                //val firebaseUser = authResult.user
                val isNewUser = authResult.additionalUserInfo!!.isNewUser
                val firebaseUser = firebaseAuth.currentUser

                if (firebaseUser != null) {

                    // get user detail like name and email id from user object
                    //getAppleUserData(user)

                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = User(uid, name, email, true, "")
                    user.isNew = isNewUser
                    user.isAuthenticated = true

                    onAuthenticated(user)
                }

            }.addOnFailureListener { e ->
                Timber.d(e, "checkPending:onFailure")
                // Toast.makeText(mActivity, mActivity.getString(R.string.signin_error), Toast.LENGTH_LONG).show()
                //onAuthenticated(User("", "", "", false, e.message))
            }
        } else {
            firebaseAuth.startActivityForSignInWithProvider(ac, provider.build())
                .addOnSuccessListener { authResult ->
                    // Sign-in successful!
                    Timber.d("activitySignIn:onSuccess:" + authResult.user)
                    val authUser = authResult.user
                    //getAppleUserData(user)
                    // get user detail like name and email id from user object

                    val isNewUser = authResult.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser

                    if (/*firebaseUser*/authUser != null) {

                        // get user detail like name and email id from user object
                        //getAppleUserData(user)

                        val uid = /*firebaseUser.uid*/ authUser.uid
                        val name = /*firebaseUser.displayName*/ authUser.displayName
                        val email = /*firebaseUser.email*/ authUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        user.isAuthenticated = true

                        onAuthenticated(user)
                    }

                }
                .addOnFailureListener { e ->
                    Timber.d("activitySignIn:onFailure")
                    onAuthenticated(User("", "", "", false, e.message))
                }
        }
    }

    fun getGalleryLow(assets: (MutableList<GalleryAssetItem>) -> Unit) {

        val assetList: MutableList<GalleryAssetItem> = mutableListOf()

        fireStoreDb.collection("assets")
            .whereEqualTo("authorUserId", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Timber.d( "${document.id}  Gallery list=> ${document.data.toPrettyJson()}")
                    try {
                        var assetItem =
                            Gson().fromJson(
                                document.data.toPrettyJson(),
                                GalleryAssetItem::class.java
                            )
                        assetList.add(assetItem)
                    } catch (e: Exception) {
                        e("Exception : $e")
                    }

                }
                assets(assetList)
            }
            .addOnFailureListener { exception ->

                e("exx $exception")
                Log.w(TAG, "Error getting documents.", exception)
            }
            .addOnCanceledListener {
                Log.w(TAG, "OnCancelListener.")
            }
            .addOnCompleteListener {
                Log.w(TAG, "OnCompleteListener.")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPoolsList(pools: (MutableList<PoolItem>) -> Unit) {

        var poolList: MutableList<PoolItem> = mutableListOf()

        fireStoreDb.collection("user_pools")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isArchived", false)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data.toPrettyJson()}")
                    var poolItem =
                        Gson().fromJson(document.data.toPrettyJson(), PoolItem::class.java)
                    poolList.add(poolItem)
                }

                poolList.sortBy { pool ->
                    var secondsToMilli = pool!!.createdAt!!.seconds!! * 1000
                    var nanoToMilli = TimeUnit.MILLISECONDS.convert(
                        pool!!.createdAt!!.nanoseconds!!.toLong(),
                        TimeUnit.NANOSECONDS
                    )
                    var total = secondsToMilli + nanoToMilli
                    total
                }

                poolList.sortBy { pool -> pool.title }
                pools(poolList)
            }
            .addOnFailureListener { exception ->

                e("exx $exception")
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun firebaseSignInWithFacebook(
        idToken: String?,
        onAuthenticated: (User) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {

                        SharedPref.loginType = LoginType.FACEBOOK.type

                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        onAuthenticated(user)
                    } else {
                        //not registered
                        onAuthenticated(User("", "", "", true, authTask.exception!!.message))
                    }
                } else {
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                }


            }
    }

    fun firebaseSignInWithEmail(
        email: String,
        intent: Intent,
        onAuthenticated: (User) -> Unit
    ) {
        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {

                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        Log.e(
                            TAG, "1111"
                        )

//                        Prefs.putString(Constants.KEY_PWD, "1234567890")
//                        Prefs.putString(Constants.KEY_LOGIN_TYPE, "email")
//                        Prefs.putString(Constants.KEY_EMAIL, email)
                        SharedPref.loginType = LoginType.EMAIL.type

                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        onAuthenticated(User(uid, name, email, true, ""))
                    } else {
                        //not registered

                        onAuthenticated(User("", "", "", true, authTask.exception!!.message))
                    }
                } else {
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))


                }
            }
    }

    private fun verifySignInLink(intent: Intent) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                Log.e(TAG, "deeplink $deepLink")

            }
            .addOnFailureListener { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
        val intent = intent
        val emailLink = intent.data.toString()

        // Confirm the link is a sign-in with email link.
        if (firebaseAuth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            val email = "someemail@domain.com"

            // The client SDK will parse the code from the link for you.
            firebaseAuth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully signed in with email link!")
                        val result = task.result

                    } else {
                        Log.e(TAG, "Error signing in with email link", task.exception)
                    }
                }
        }
    }

    fun firebaseSignUpWithEmail(
        email: String,
        onAuthenticated: (User) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, "1234567890")
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
//
//                        Prefs.putString(Constants.KEY_PWD, "1234567890")
//                        Prefs.putString(Constants.KEY_LOGIN_TYPE, "email")
//                        Prefs.putString(Constants.KEY_EMAIL, email)

                        SharedPref.loginType = LoginType.EMAIL.type


                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        onAuthenticated(user)
                    } else {
                        //not registered
                        onAuthenticated(User("", "", "", true, authTask.exception!!.message))
                    }
                } else {
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                }


            }
    }

    fun firebaseSignUpWithEmailPassword(
        email: String,
        password: String,
        onAuthenticated: (User) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {

                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        onAuthenticated(user)
                    } else {
                        //not registered
                        onAuthenticated(User("", "", "", true, authTask.exception!!.message))
                    }
                } else {
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                }


            }
    }

    fun firebaseLoginWithEmailPassword(
        email: String,
        password: String,
        onAuthenticated: (User) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {

                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = User(uid, name, email, true, "")
                        user.isNew = isNewUser
                        onAuthenticated(user)

                    } else {
                        //not registered
                        onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    }
                } else {
                    onAuthenticated(User("", "", "", false, authTask.exception!!.message))
                    Log.e(
                        TAG, authTask.exception!!.message.toString()
                    )
                }

            }
    }

    companion object {
        private const val TAG = "Group Photo Service : "
    }
}
