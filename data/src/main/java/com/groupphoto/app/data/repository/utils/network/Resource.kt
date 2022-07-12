package com.groupphoto.app.data.repository.utils.network


sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val errorResponse: ErrorResponse) : Resource<Nothing>()
}

