package com.saveetha.cineselect.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    @SerializedName("favorite_genres")
    val favoriteGenres: List<Genre> = emptyList(),
    @SerializedName("watchlist_count")
    val watchlistCount: Int = 0
)