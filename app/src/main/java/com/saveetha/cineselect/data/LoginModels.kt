package com.saveetha.cineselect.data

// Note: LoginRequest is not needed for a FormUrlEncoded request,
// but LoginResponse is needed to parse the success response.

data class LoginResponse(
    val access_token: String,
    val token_type: String
)
