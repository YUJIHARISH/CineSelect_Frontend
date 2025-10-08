package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saveetha.cineselect.data.RecentSearchAdapter
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class ExploreActivity : AppCompatActivity() {

    private lateinit var rvRecentSearches: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        initializeViews()
        setupTopBar()
        setupPopularGenres()
        setupBottomNavigation()
        loadRecentSearches()

        val advancedSearch = findViewById<Button>(R.id.btnAdvancedSearch)
        advancedSearch.setOnClickListener {
            val intent = Intent(this, AdvancedSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeViews() {
        rvRecentSearches = findViewById(R.id.rvRecentSearches)
        rvRecentSearches.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        NavigationHelper.navigateBack(this)
    }

    private fun setupTopBar() {
        val back = findViewById<ImageView>(R.id.ivBack)
        back.setOnClickListener { NavigationHelper.navigateBack(this) }

        val search = findViewById<EditText>(R.id.etSearch)
        search.setOnEditorActionListener { v, _, _ ->
            val query = v.text?.toString()?.trim().orEmpty()
            if (query.isNotEmpty()) {
                searchMovies(query)
                v.text = null
            }
            true
        }
    }

    private fun searchMovies(query: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("SEARCH_QUERY", query)
        startActivity(intent)
    }

    private fun loadRecentSearches() {
        lifecycleScope.launch {
            try {
                val recentSearches = ApiClient.getInstance(this@ExploreActivity).getRecentSearches()
                rvRecentSearches.adapter = RecentSearchAdapter(recentSearches.map { it.query }) { searchQuery ->
                    searchMovies(searchQuery)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ExploreActivity, "Failed to load recent searches", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPopularGenres() {
        val chips = mapOf(
            R.id.genreAction to (28 to "Action"),
            R.id.genreComedy to (35 to "Comedy"),
            R.id.genreDrama to (18 to "Drama"),
            R.id.genreHorror to (27 to "Horror"),
            R.id.genreSciFi to (878 to "Sci-Fi"),
            R.id.genreRomance to (10749 to "Romance")
        )

        chips.forEach { (id, pair) ->
            val (genreId, genreName) = pair
            findViewById<TextView>(id).setOnClickListener {
                val intent = Intent(this, SearchResultsActivity::class.java)
                intent.putExtra("GENRE_ID", genreId)
                intent.putExtra("GENRE_NAME", genreName)
                startActivity(intent)
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_explore
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    NavigationHelper.navigateTo(this, HomeActivity::class.java)
                    true
                }
                R.id.navigation_explore -> true
                R.id.navigation_watchlist -> {
                    NavigationHelper.navigateTo(this, MyWatchlistActivity::class.java)
                    true
                }
                R.id.navigation_profile -> {
                    NavigationHelper.navigateTo(this, ProfileActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
