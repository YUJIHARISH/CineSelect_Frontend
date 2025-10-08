package com.saveetha.cineselect.network

import com.google.gson.Gson
import com.saveetha.cineselect.data.MovieSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

suspend fun ApiClient.searchMoviesByActor(actorName: String, page: Int): MovieSearchResponse {
    // This is a simplified implementation for demonstration.
    // A real app would use a robust networking library like Retrofit or Ktor.
    val urlEncodedActorName = java.net.URLEncoder.encode(actorName, "UTF-8")
    val url = URL("${BASE_URL}api/actors/search/$urlEncodedActorName?page=$page")

    return withContext(Dispatchers.IO) {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()

        // Use Gson to parse the JSON response into our data classes
        Gson().fromJson(response, MovieSearchResponse::class.java)
    }
}