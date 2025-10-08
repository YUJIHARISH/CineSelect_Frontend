package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.slider.RangeSlider
import com.saveetha.cineselect.data.Genre
import com.saveetha.cineselect.data.GenreAdapter
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class AdvancedSearchActivity : AppCompatActivity() {

    private lateinit var genreAdapter: GenreAdapter
    private val selectedGenres = mutableSetOf<Genre>()
    private lateinit var rvGenres: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search)

        setupTopBar()
        setupLanguageSpinner()
        setupSliders()
        setupGenreRecyclerView()
        setupSearchButton()
        fetchGenres()
    }

    private fun setupTopBar() {
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupLanguageSpinner() {
        val spinner = findViewById<Spinner>(R.id.spLanguage)
        val languages = listOf(
            "All Languages", "en", "es", "fr", "de", "it", "ja", "ko", "zh", "hi", "ta"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSliders() {
        val yearSlider = findViewById<RangeSlider>(R.id.sliderYear)
        val tvYearStart = findViewById<TextView>(R.id.tvYearStart)
        val tvYearEnd = findViewById<TextView>(R.id.tvYearEnd)

        yearSlider.valueFrom = 1950f
        yearSlider.valueTo = 2025f
        yearSlider.values = listOf(2000f, 2025f)
        updateYearLabels(yearSlider.values, tvYearStart, tvYearEnd)
        yearSlider.addOnChangeListener { slider, _, _ ->
            updateYearLabels((slider as RangeSlider).values, tvYearStart, tvYearEnd)
        }
    }

    private fun updateYearLabels(values: List<Float>, start: TextView, end: TextView) {
        val startYear = values.first().toInt()
        val endYear = values.last().toInt()
        start.text = startYear.toString()
        end.text = endYear.toString()
    }

    private fun setupGenreRecyclerView() {
        rvGenres = findViewById(R.id.rvGenres)
        rvGenres.layoutManager = FlexboxLayoutManager(this)
        genreAdapter = GenreAdapter(mutableListOf()) { genre ->
            if (selectedGenres.contains(genre)) {
                selectedGenres.remove(genre)
            } else {
                if (selectedGenres.size < 5) {
                    selectedGenres.add(genre)
                } else {
                    Toast.makeText(this, "You can select up to 5 genres", Toast.LENGTH_SHORT).show()
                }
            }
            genreAdapter.selectedGenres = selectedGenres
            genreAdapter.notifyDataSetChanged()
        }
        rvGenres.adapter = genreAdapter
    }

    private fun fetchGenres() {
        lifecycleScope.launch {
            try {
                val genres = ApiClient.getInstance(this@AdvancedSearchActivity).getGenres()
                genreAdapter.updateData(genres)
            } catch (e: Exception) {
                Toast.makeText(this@AdvancedSearchActivity, "Failed to load genres", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchButton() {
        val btn = findViewById<Button>(R.id.btnSearch)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val yearSlider = findViewById<RangeSlider>(R.id.sliderYear)
        val language = findViewById<Spinner>(R.id.spLanguage)

        btn.setOnClickListener {
            val years = yearSlider.values.map { it.toInt() }
            val rating = ratingBar.rating

            val lang = if (language.selectedItem.toString() == "All Languages") null else language.selectedItem.toString()

            val selectedGenreIds = selectedGenres.map { it.id }.joinToString(",")

            val intent = Intent(this, SearchResultsActivity::class.java)
            intent.putExtra("GENRES", selectedGenreIds)
            intent.putExtra("RATING_GTE", rating)
            intent.putExtra("RELEASE_YEAR_GTE", years.first())
            intent.putExtra("RELEASE_YEAR_LTE", years.last())
            intent.putExtra("LANGUAGE", lang)
            startActivity(intent)
        }
    }
}
