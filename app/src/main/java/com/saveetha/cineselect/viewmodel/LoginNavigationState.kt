package com.saveetha.cineselect.viewmodel

sealed class LoginNavigationState {
    object GoToHome : LoginNavigationState()
    object GoToGenres : LoginNavigationState()
    data class Error(val message: String) : LoginNavigationState()
}