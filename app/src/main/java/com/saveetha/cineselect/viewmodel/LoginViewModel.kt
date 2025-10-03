package com.saveetha.cineselect.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.saveetha.cineselect.data.LoginResponse
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException




data class ErrorResponse(val detail: String)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginResult = MutableLiveData<LoginNavigationState>()
    val loginResult: LiveData<LoginNavigationState> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val loginResponse = ApiClient.getInstance(getApplication()).login(username, password)
                com.saveetha.cineselect.network.AuthTokenManager.saveToken(getApplication(), loginResponse.access_token)

                val userProfile = ApiClient.getInstance(getApplication()).getMyProfile()

                if (userProfile.favoriteGenres.isNullOrEmpty()) {
                    _loginResult.postValue(LoginNavigationState.GoToGenres)
                } else {
                    _loginResult.postValue(LoginNavigationState.GoToHome)
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).detail
                } catch (jsonError: Exception) {
                    errorBody ?: "An unknown HTTP error occurred"
                }
                Log.e("LoginViewModel", "HTTP Error: $errorMessage")
                _loginResult.postValue(LoginNavigationState.Error(errorMessage))

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login API call failed", e)
                _loginResult.postValue(LoginNavigationState.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
}
