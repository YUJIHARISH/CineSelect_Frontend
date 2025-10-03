package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.saveetha.cineselect.data.MoviePreview
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var btnPreviousPage: ImageButton
    private lateinit var btnNextPage: ImageButton
    private lateinit var tvPageNumber: TextView

    private var currentPage = 1
    private var totalPages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        setupGenreMixes()
        setupObservers()

        homeViewModel.fetchRecommendedMovies()
    }

    private fun initializeViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        movieAdapter = MovieAdapter(emptyList())
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        btnPreviousPage = findViewById(R.id.btnPreviousPage)
        btnNextPage = findViewById(R.id.btnNextPage)
        tvPageNumber = findViewById(R.id.tvPageNumber)
    }

    private fun setupClickListeners() {
        btnNextPage.setOnClickListener {
            if (currentPage < totalPages) {
                homeViewModel.fetchRecommendedMovies(currentPage + 1)
            }
        }

        btnPreviousPage.setOnClickListener {
            if (currentPage > 1) {
                homeViewModel.fetchRecommendedMovies(currentPage - 1)
            }
        }
    }

    private fun setupObservers() {
        homeViewModel.movies.observe(this, Observer { movies ->
            movieAdapter.updateMovies(movies)
        })

        homeViewModel.currentPage.observe(this, Observer { page ->
            currentPage = page
            updatePaginationControls()
        })

        homeViewModel.totalPages.observe(this, Observer { pages ->
            totalPages = pages
            updatePaginationControls()
        })
    }

    private fun updatePaginationControls() {
        tvPageNumber.text = "Page $currentPage / $totalPages"
        btnPreviousPage.isEnabled = currentPage > 1
        btnNextPage.isEnabled = currentPage < totalPages
    }

    private fun setupGenreMixes() {
        findViewById<TextView>(R.id.tvMix1).setOnClickListener { openGenreMix("Action + Thriller", "28,53") }
        findViewById<TextView>(R.id.tvMix2).setOnClickListener { openGenreMix("Animation + Family", "16,10751") }
        findViewById<TextView>(R.id.tvMix3).setOnClickListener { openGenreMix("Comedy + Romance", "35,10749") }
        findViewById<TextView>(R.id.tvMix4).setOnClickListener { openGenreMix("Sci-Fi + Adventure", "878,12") }
        findViewById<TextView>(R.id.tvMix5).setOnClickListener { openGenreMix("Drama + Mystery", "18,9648") }
    }

    private fun openGenreMix(title: String, genreIds: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("SELECTED_GENRES", genreIds)
        intent.putExtra("GENRE_NAME", title)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_home
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_explore -> { NavigationHelper.navigateTo(this, ExploreActivity::class.java); true }
                R.id.navigation_watchlist -> { NavigationHelper.navigateTo(this, MyWatchlistActivity::class.java); true }
                R.id.navigation_profile -> { NavigationHelper.navigateTo(this, ProfileActivity::class.java); true }
                else -> false
            }
        }
    }

    // Using a nested adapter class to ensure consistency
    inner class MovieAdapter(private var movieList: List<MoviePreview>) :
        RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

        inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.ivMoviePoster)
            val tvTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
            val tvYear: TextView = itemView.findViewById(R.id.tvMovieYear)
            val tvRating: TextView = itemView.findViewById(R.id.tvMovieRating)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_result, parent, false)
            return MovieViewHolder(view)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            val movie = movieList[position]
            holder.imageView.load(movie.poster_path) {
                crossfade(true)
                placeholder(R.drawable.movie_poster_placeholder)
                error(R.drawable.ic_broken_image)
            }
            holder.tvTitle.text = movie.title
            holder.tvYear.text = movie.release_date?.split("-")?.firstOrNull() ?: "N/A"
            holder.tvRating.text = String.format("%.1f", movie.vote_average ?: 0.0f)

            holder.itemView.setOnClickListener {
                val intent = Intent(this@HomeActivity, MovieOverviewActivity::class.java).apply {
                    putExtra(MovieOverviewActivity.EXTRA_MOVIE_ID, movie.id)
                }
                startActivity(intent)
            }
        }

        override fun getItemCount() = movieList.size

        fun updateMovies(newMovies: List<MoviePreview>) {
            movieList = newMovies
            notifyDataSetChanged()
        }
    }
}
