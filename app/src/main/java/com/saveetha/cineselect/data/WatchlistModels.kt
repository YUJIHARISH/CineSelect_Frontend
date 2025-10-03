package com.saveetha.cineselect.data

data class Watchlist(
    val id: Int,
    val name: String,
    val movies: List<MovieDetails>
)

data class MovieId(val movieId: Int)

data class RecentSearch(
    val id: Int,
    val query: String,
    val created_at: String
)
