package com.saveetha.cineselect.viewmodel

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saveetha.cineselect.data.RegisterRequest
import com.saveetha.cineselect.data.LoginResponse
import com.saveetha.cineselect.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log // Import Log for debugging
import com.saveetha.cineselect.network.ApiClient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.saveetha.cineselect.network.NetworkResult

class SignupViewModel(application: Application) : AndroidViewModel(application) {

    private val _signupResult = MutableLiveData<NetworkResult<LoginResponse>>()
    val signupResult: LiveData<NetworkResult<LoginResponse>> = _signupResult

    fun signup(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getInstance(getApplication()).register(RegisterRequest(fullName, email, password))
                _signupResult.postValue(NetworkResult.Success(response))
            } catch (e: Exception) {
                _signupResult.postValue(NetworkResult.Error(e))
            }
        }
    }
}