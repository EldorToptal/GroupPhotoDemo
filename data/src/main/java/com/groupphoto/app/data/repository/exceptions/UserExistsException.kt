package com.groupphoto.app.data.repository.exceptions

class UserExistsException : Exception(){
    override val message: String?
        get() = super.message
}