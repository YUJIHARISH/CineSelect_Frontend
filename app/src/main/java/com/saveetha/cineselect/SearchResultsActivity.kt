package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.doOnLayout
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saveetha.cineselect.data.MoviePreview
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity() {

    // Declare all UI elements
    private lateinit var tvResultsTitle: TextView
    private lateinit var cardSaveSearch: CardView
    private lateinit var btnFilter: LinearLayout
    private lateinit var btnSort: LinearLayout
    private lateinit var tvMovieCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var selectedTabIndicator: View
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var tvEmptyMessage: TextView
    private lateinit var btnRetry: Button
    private lateinit var btnPreviousPage: ImageButton
    private lateinit var btnNextPage: ImageButton
    private lateinit var tvPageNumber: TextView


    // Pagination state
    private var currentPage = 1
    private var totalPages = 1

    // Data and Adapter
    private var currentMovies = mutableListOf<MoviePreview>()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        setupRecyclerView()
        applySearchFilters() // Use the intent extras to fetch movies
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initializeViews() {
        tvResultsTitle = findViewById(R.id.tvResultsTitle)
        cardSaveSearch = findViewById(R.id.cardSaveSearch)
        btnFilter = findViewById(R.id.btnFilter)
        btnSort = findViewById(R.id.btnSort)
        tvMovieCount = findViewById(R.id.tvMovieCount)
        recyclerView = findViewById(R.id.recyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        selectedTabIndicator = findViewById(R.id.selectedTabIndicator)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        btnRetry = findViewById(R.id.btnRetry)
        btnPreviousPage = findViewById(R.id.btnPreviousPage)
        btnNextPage = findViewById(R.id.btnNextPage)
        tvPageNumber = findViewById(R.id.tvPageNumber)

        val searchQuery = intent.getStringExtra("SEARCH_QUERY")
        val genreName = intent.getStringExtra("GENRE_NAME")
        val genres = intent.getStringExtra("GENRES")

        if (!searchQuery.isNullOrEmpty()) {
            tvResultsTitle.text = "Results: $searchQuery"
        } else if (!genreName.isNullOrEmpty()) {
            tvResultsTitle.text = genreName
        } else if (genres != null && genres.isNotEmpty()) {
            tvResultsTitle.text = "Advanced Search Results"
        } else {
            tvResultsTitle.text = "Search Results"
        }
    }

    private fun setupClickListeners() {
        findViewById<ImageButton>(R.id.ivBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        cardSaveSearch.setOnClickListener {
            Toast.makeText(this, "Search saved!", Toast.LENGTH_SHORT).show()
        }
        btnFilter.setOnClickListener {
            val intent = Intent(this, AdvancedSearchActivity::class.java)
            startActivity(intent)
        }
        btnSort.setOnClickListener {
            showSortOptions()
        }

        btnRetry.setOnClickListener {
            applySearchFilters(page = currentPage)
        }

        btnNextPage.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                applySearchFilters(page = currentPage)
                recyclerView.smoothScrollToPosition(0)
            }
        }

        btnPreviousPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                applySearchFilters(page = currentPage)
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(currentMovies)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = movieAdapter
    }

    private fun applySearchFilters(sortBy: String? = null, page: Int = 1) {
        val genreId = intent.getIntExtra("GENRE_ID", -1)
        val selectedGenres = intent.getStringExtra("GENRES")
        val searchQuery = intent.getStringExtra("SEARCH_QUERY")
        val ratingGte = intent.getFloatExtra("RATING_GTE", 0f)
        val releaseYearGte = intent.getIntExtra("RELEASE_YEAR_GTE", 0)
        val releaseYearLte = intent.getIntExtra("RELEASE_YEAR_LTE", 0)
        val language = intent.getStringExtra("LANGUAGE")

        val genres: String? = when {
            !selectedGenres.isNullOrEmpty() -> selectedGenres
            genreId != -1 -> genreId.toString()
            else -> null
        }

        fetchMovies(
            query = searchQuery,
            genres = genres,
            sortBy = sortBy,
            page = page,
            ratingGte = if (ratingGte > 0) ratingGte else null,
            releaseYearGte = if (releaseYearGte != 0) releaseYearGte else null,
            releaseYearLte = if (releaseYearLte != 0) releaseYearLte else null,
            language = if (language != "All Languages" && !language.isNullOrEmpty()) language else null
        )
    }

    private fun fetchMovies(
        query: String? = null,
        genres: String? = null,
        sortBy: String? = null,
        page: Int = 1,
        ratingGte: Float? = null,
        releaseYearGte: Int? = null,
        releaseYearLte: Int? = null,
        language: String? = null
    ) {
        loadingProgressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmptyMessage.visibility = View.GONE
        btnRetry.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movieSearchResponse = if (query != null) {
                    ApiClient.getInstance(this@SearchResultsActivity).searchNlp(query, page)
                } else {
                    ApiClient.getInstance(this@SearchResultsActivity).discoverMovies(
                        genres = genres,
                        sortBy = sortBy, 
                        page = page,
                        ratingGte = ratingGte,
                        releaseYearGte = releaseYearGte,
                        releaseYearLte = releaseYearLte,
                        language = language
                    )
                }

                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    currentPage = movieSearchResponse.page
                    totalPages = movieSearchResponse.totalPages
                    currentMovies.clear()
                    val movies = movieSearchResponse.results
                    if (movies.isNotEmpty()) {
                        currentMovies.addAll(movies)
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        tvEmptyMessage.text = "No movies found."
                        tvEmptyMessage.visibility = View.VISIBLE
                    }
                    movieAdapter.notifyDataSetChanged()
                    tvMovieCount.text = "${movieSearchResponse.totalResults} movies found"
                    updatePaginationControls()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    tvEmptyMessage.text = "Network Error: Please try again."
                    tvEmptyMessage.visibility = View.VISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
                e.printStackTrace()
            }
        }
    }

    private fun showSortOptions() {
        val sortOptions = arrayOf("Rating (High to Low)", "Rating (Low to High)", "Year (Newest)", "Year (Oldest)", "Title A-Z", "Title Z-A")
        AlertDialog.Builder(this)
            .setTitle("Sort by")
            .setItems(sortOptions) { _, which ->
                val sortByParam = when (which) {
                    0 -> "rating.desc"
                    1 -> "rating.asc"
                    2 -> "release_date.desc"
                    3 -> "release_date.asc"
                    4 -> "title.asc"
                    5 -> "title.desc"
                    else -> null
                }
                applySearchFilters(sortBy = sortByParam)
                Toast.makeText(this, "Sorted by: ${sortOptions[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_explore

        bottomNavigation.doOnLayout {
            val exploreItem = bottomNavigation.menu.findItem(R.id.navigation_explore)

            var exploreItemIndex = -1
            for (i in 0 until bottomNavigation.menu.size()) {
                if (bottomNavigation.menu.getItem(i).itemId == exploreItem.itemId) {
                    exploreItemIndex = i
                    break
                }
            }

            if (exploreItemIndex != -1) {
                val tabWidth = bottomNavigation.width / bottomNavigation.menu.size()
                val indicatorWidth = selectedTabIndicator.width
                val params = selectedTabIndicator.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = exploreItemIndex * tabWidth + (tabWidth - indicatorWidth) / 2
                selectedTabIndicator.layoutParams = params
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_explore -> true // Already on this screen
                R.id.navigation_watchlist -> {
                    startActivity(Intent(this, MyWatchlistActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updatePaginationControls() {
        tvPageNumber.text = "Page $currentPage / $totalPages"
        btnPreviousPage.isEnabled = currentPage > 1
        btnNextPage.isEnabled = currentPage < totalPages
    }

    inner class MovieAdapter(private val movieList: List<MoviePreview>) :
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
            // Use the full URL for the poster
            holder.imageView.load(movie.poster_path) {
                crossfade(true)
                placeholder(R.drawable.movie_poster_placeholder)
                error(R.drawable.ic_broken_image)
            }
            holder.tvTitle.text = movie.title
            holder.tvYear.text = movie.release_date?.split("-")?.firstOrNull() ?: "N/A"
            // Correctly format the rating, handling nulls
            holder.tvRating.text = String.format("%.1f", movie.vote_average ?: 0.0f)

            holder.itemView.setOnClickListener {
                val intent = Intent(this@SearchResultsActivity, MovieOverviewActivity::class.java).apply {
                    putExtra(MovieOverviewActivity.EXTRA_MOVIE_ID, movie.id)
                }
                startActivity(intent)
            }
        }

        override fun getItemCount() = movieList.size
    }
}
