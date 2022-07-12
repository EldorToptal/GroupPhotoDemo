package com.groupphoto.app.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.CommonStatusCodes
import com.groupphoto.app.data.repository.utils.network.LiveEvent
import com.squareup.okhttp.internal.http.RequestException
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class BaseViewModel : ViewModel() {
    val errorOther: LiveEvent<String> = LiveEvent()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)


    val handler = CoroutineExceptionHandler { _, exception ->
        errorProcess(exception)
    }

    val vmScope = viewModelScope + handler + Dispatchers.IO

    fun errorProcess(throwable: Throwable, f: ((t: Throwable) -> Unit)? = null) {
        viewModelScope.launch {
            loading.value = false
            errorOther.postValue(throwable.message)
        }
    }
}