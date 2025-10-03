package com.saveetha.cineselect.network

import com.saveetha.cineselect.data.Genre
import com.saveetha.cineselect.data.LoginResponse
import com.saveetha.cineselect.data.MovieSearchResponse
import com.saveetha.cineselect.data.RegisterRequest

import com.saveetha.cineselect.data.UserPreferences
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

import com.saveetha.cineselect.data.MovieDetails
import retrofit2.http.Path

interface ApiService {
    @GET("api/genres")
    suspend fun getGenres(): List<Genre>

    @GET("api/movies/{id}")
    suspend fun getMovieDetails(@Path("id") movieId: Int): MovieDetails
    @GET("api/movies/trending-mixes")
    suspend fun getTrendingMixes(): MovieSearchResponse

    @GET("api/movies/recommendations/for-you")
    suspend fun getRecommendationsForYou(@Query("page") page: Int): MovieSearchResponse

    @GET("api/users/me")
    suspend fun getMyProfile(): com.saveetha.cineselect.data.User

    @PUT("api/users/me/preferences")
    fun updateUserPreferences(@Body preferences: UserPreferences): Call<Unit>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("api/watchlists/")
    suspend fun getWatchlists(): List<com.saveetha.cineselect.data.Watchlist>

    @POST("api/watchlists/add")
    suspend fun addMovieToWatchlist(@Body movie: com.saveetha.cineselect.data.MovieId): com.saveetha.cineselect.data.Watchlist

    @POST("api/watchlists/remove")
    suspend fun removeMovieFromWatchlist(@Body movie: com.saveetha.cineselect.data.MovieId): com.saveetha.cineselect.data.Watchlist

    @GET("api/activity/recent-searches")
    suspend fun getRecentSearches(): List<com.saveetha.cineselect.data.RecentSearch>

    @GET("api/movies/search/nlp")
    suspend fun searchNlp(@Query("q") query: String, @Query("page") page: Int): MovieSearchResponse

    @GET("api/movies/discover")
    suspend fun discoverMovies(
        @Query("genres") genres: String? = null,
        @Query("release_year_gte") releaseYearGte: Int? = null,
        @Query("release_year_lte") releaseYearLte: Int? = null,
        @Query("rating_gte") ratingGte: Float? = null,
        @Query("language") language: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("page") page: Int? = null
    ): MovieSearchResponse

    @GET("api/movies/search")
    suspend fun searchMovies(
        @Query("q") query: String?,
        @Query("genres") genres: String?,
        @Query("releaseYear_gte") releaseYearGte: Int?,
        @Query("releaseYear_lte") releaseYearLte: Int?,
        @Query("rating_gte") ratingGte: Float?,
        @Query("sortBy") sortBy: String?,
        @Query("page") page: Int?
    ): MovieSearchResponse
}



