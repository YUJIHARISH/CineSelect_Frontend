package com.saveetha.cineselect.data

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    val runtime: Int?,
    val certification: String?,
    val genres: List<Genre>?,
    val credits: Credits?
)

data class MoviePreview(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double?
)

data class MovieSearchResponse(
    val results: List<MoviePreview>,
    val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)