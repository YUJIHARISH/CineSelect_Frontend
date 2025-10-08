package com.saveetha.cineselect

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saveetha.cineselect.data.MovieDetails
import com.saveetha.cineselect.data.MovieId
import com.saveetha.cineselect.data.WatchlistAdapter
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class MyWatchlistActivity : AppCompatActivity() {

    private lateinit var rvWatchlist: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: WatchlistAdapter
    private lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_watchlist)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        loadWatchlist()
    }

    private fun initializeViews() {
        rvWatchlist = findViewById(R.id.rvWatchlist)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        ivBack = findViewById(R.id.ivBack)
        rvWatchlist.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadWatchlist() {
        lifecycleScope.launch {
            try {
                val watchlists = ApiClient.getInstance(this@MyWatchlistActivity).getWatchlists()
                val watchlist = watchlists.firstOrNull()
                if (watchlist != null && watchlist.movies.isNotEmpty()) {
                    adapter = WatchlistAdapter(watchlist.movies.toMutableList()) { movie ->
                        removeMovieFromWatchlist(movie)
                    }
                    rvWatchlist.adapter = adapter
                } else {
                    Toast.makeText(this@MyWatchlistActivity, "Your watchlist is empty.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("MyWatchlistActivity", "Failed to load watchlist", e)
                Toast.makeText(this@MyWatchlistActivity, "Failed to load watchlist.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeMovieFromWatchlist(movie: MovieDetails) {
        lifecycleScope.launch {
            try {
                ApiClient.getInstance(this@MyWatchlistActivity).removeMovieFromWatchlist(MovieId(movie.id))
                adapter.removeItem(movie)
                Toast.makeText(this@MyWatchlistActivity, "Removed from watchlist", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MyWatchlistActivity", "Failed to remove movie", e)
                Toast.makeText(this@MyWatchlistActivity, "Failed to remove movie", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_watchlist
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    NavigationHelper.navigateTo(this, HomeActivity::class.java)
                    true
                }
                R.id.navigation_explore -> {
                    NavigationHelper.navigateTo(this, ExploreActivity::class.java)
                    true
                }
                R.id.navigation_watchlist -> true
                R.id.navigation_profile -> {
                    NavigationHelper.navigateTo(this, ProfileActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
