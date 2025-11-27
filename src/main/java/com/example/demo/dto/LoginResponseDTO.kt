package com.example.demo.dto

import com.example.demo.controller.UserController.UserDto

class LoginResponseDTO {
    var success = false
    var message: String? = null
    var idToken: String? = null
    var refreshToken: String? = null
    var firebaseUid: String? = null
    var user: UserDto? = null

    constructor()

    constructor(success: Boolean, message: String?) {
        this.success = success
        this.message = message
    }

    constructor(
        success: Boolean,
        message: String?,
        idToken: String?,
        refreshToken: String?,
        firebaseUid: String?,
        user: UserDto?
    ) {
        this.success = success
        this.message = message
        this.idToken = idToken
        this.refreshToken = refreshToken
        this.firebaseUid = firebaseUid
        this.user = user
    }
}
