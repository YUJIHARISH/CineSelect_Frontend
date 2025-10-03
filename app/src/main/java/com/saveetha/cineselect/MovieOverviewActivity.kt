package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.saveetha.cineselect.data.CastAdapter
import com.saveetha.cineselect.data.Genre
import com.saveetha.cineselect.data.MovieDetails
import com.saveetha.cineselect.data.MovieId
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class MovieOverviewActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivShare: ImageView
    private lateinit var ivMoviePoster: ImageView
    private lateinit var btnAddToWatchlist: Button
    private lateinit var btnWhereToWatch: Button
    private lateinit var tvMovieTitle: TextView
    private lateinit var tvMovieDetails: TextView
    private lateinit var tvOverview: TextView
    private lateinit var genreChipsContainer: LinearLayout
    private lateinit var rvCast: RecyclerView

    private var isMovieInWatchlist = false
    private var movieId: Int = -1
    private var movieTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_overview)

        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        if (movieId == -1) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        setupClickListeners()
        loadMovieData()
        checkWatchlistStatus()
    }

    private fun initializeViews() {
        ivBack = findViewById(R.id.ivBack)
        ivShare = findViewById(R.id.ivShare)
        ivMoviePoster = findViewById(R.id.ivHeroImage)
        btnAddToWatchlist = findViewById(R.id.btnAddToWatchlist)
        btnWhereToWatch = findViewById(R.id.btnWhereToWatch)
        tvMovieTitle = findViewById(R.id.tvMovieTitle)
        tvMovieDetails = findViewById(R.id.tvMovieDetails)
        tvOverview = findViewById(R.id.tvOverview)
        genreChipsContainer = findViewById(R.id.genreChipsContainer)
        rvCast = findViewById(R.id.rvCast)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        ivShare.setOnClickListener { shareMovie() }
        btnAddToWatchlist.setOnClickListener { toggleWatchlist() }
        btnWhereToWatch.setOnClickListener { toggleWhereToWatch() }
    }

    private fun loadMovieData() {
        lifecycleScope.launch {
            try {
                val movie = ApiClient.getInstance(this@MovieOverviewActivity).getMovieDetails(movieId)
                movieTitle = movie.title
                tvMovieTitle.text = movie.title
                tvOverview.text = movie.overview

                val year = movie.releaseDate?.substring(0, 4) ?: "N/A"
                val runtime = movie.runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A"
                val rating = movie.certification ?: "N/A"
                tvMovieDetails.text = "$year • $runtime • $rating"

                populateGenreChips(movie.genres)

                movie.credits?.cast?.let {
                    rvCast.layoutManager = LinearLayoutManager(this@MovieOverviewActivity)
                    rvCast.adapter = CastAdapter(it)
                }

                ivMoviePoster.load(movie.posterPath) {
                    placeholder(R.drawable.movie_poster_placeholder)
                    error(R.drawable.movie_poster_placeholder)
                }
            } catch (e: Exception) {
                Log.e("MovieOverviewActivity", "Failed to load movie details", e)
                Toast.makeText(this@MovieOverviewActivity, "Failed to load movie details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkWatchlistStatus() {
        lifecycleScope.launch {
            try {
                val watchlists = ApiClient.getInstance(this@MovieOverviewActivity).getWatchlists()
                // Assuming the user has only one watchlist
                val mainWatchlist = watchlists.firstOrNull()
                if (mainWatchlist != null) {
                    isMovieInWatchlist = mainWatchlist.movies.any { it.id == movieId }
                    updateWatchlistButtonState()
                }
            } catch (e: Exception) {
                Log.e("MovieOverviewActivity", "Failed to check watchlist status", e)
            }
        }
    }

    private fun toggleWatchlist() {
        lifecycleScope.launch {
            try {
                val movieRequest = MovieId(movieId)
                if (isMovieInWatchlist) {
                    ApiClient.getInstance(this@MovieOverviewActivity).removeMovieFromWatchlist(movieRequest)
                    Toast.makeText(this@MovieOverviewActivity, "$movieTitle removed from watchlist", Toast.LENGTH_SHORT).show()
                } else {
                    ApiClient.getInstance(this@MovieOverviewActivity).addMovieToWatchlist(movieRequest)
                    Toast.makeText(this@MovieOverviewActivity, "$movieTitle added to watchlist!", Toast.LENGTH_SHORT).show()
                }
                isMovieInWatchlist = !isMovieInWatchlist
                updateWatchlistButtonState()
            } catch (e: Exception) {
                Log.e("MovieOverviewActivity", "Failed to update watchlist", e)
                Toast.makeText(this@MovieOverviewActivity, "Error updating watchlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateWatchlistButtonState() {
        btnAddToWatchlist.isSelected = isMovieInWatchlist
        if (isMovieInWatchlist) {
            btnAddToWatchlist.text = "In Watchlist"
            btnAddToWatchlist.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            btnAddToWatchlist.text = "Add to Watchlist"
            btnAddToWatchlist.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
        }
    }

    private fun populateGenreChips(genres: List<Genre>?) {
        genreChipsContainer.removeAllViews()
        genres?.forEach { genre ->
            val genreChip = TextView(this).apply {
                text = genre.name
                background = ContextCompat.getDrawable(this@MovieOverviewActivity, R.drawable.bg_selected_genre)
                setTextColor(ContextCompat.getColor(this@MovieOverviewActivity, R.color.white))
                textSize = 14f
                setPadding(40, 20, 40, 20)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 30
                }
                layoutParams = params
            }
            genreChipsContainer.addView(genreChip)
        }
    }

    private fun shareMovie() {
        val movieTitle = tvMovieTitle.text.toString()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this movie: $movieTitle")
            putExtra(Intent.EXTRA_SUBJECT, "Movie Recommendation")
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to share", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleWhereToWatch() {
        btnWhereToWatch.isSelected = !btnWhereToWatch.isSelected

        if (btnWhereToWatch.isSelected) {
            btnWhereToWatch.setTextColor(ContextCompat.getColor(this, R.color.white))
            Toast.makeText(this, "Finding where to watch $movieTitle...", Toast.LENGTH_SHORT).show()
        } else {
            btnWhereToWatch.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
        }
    }

    companion object {
        const val EXTRA_MOVIE_ID = "MOVIE_ID"
    }
}