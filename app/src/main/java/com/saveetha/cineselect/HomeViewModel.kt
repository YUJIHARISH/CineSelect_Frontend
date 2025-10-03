package com.saveetha.cineselect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saveetha.cineselect.data.MoviePreview
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _movies = MutableLiveData<List<MoviePreview>>()
    val movies: LiveData<List<MoviePreview>> = _movies

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    fun fetchRecommendedMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val movieResponse = ApiClient.getInstance(getApplication()).getRecommendationsForYou(page)
                _movies.postValue(movieResponse.results)
                _currentPage.postValue(movieResponse.page)
                _totalPages.postValue(movieResponse.totalPages)
            } catch (e: Exception) {
                // Log error, but don't crash
                android.util.Log.e("HomeViewModel", "Error fetching recommended movies", e)
            }
        }
    }
}
