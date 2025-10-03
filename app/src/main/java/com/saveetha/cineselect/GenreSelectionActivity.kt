package com.saveetha.cineselect

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.saveetha.cineselect.R
import com.saveetha.cineselect.network.ApiClient
import com.saveetha.cineselect.data.UserPreferences



import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenreSelectionActivity : AppCompatActivity() {

    private val genreIds = intArrayOf(
        R.id.genreAction, R.id.genreComedy, R.id.genreDrama,
        R.id.genreHorror, R.id.genreSciFi, R.id.genreRomance,
        R.id.genreThriller, R.id.genreAnimation, R.id.genreFantasy,
        R.id.genreDocumentary, R.id.genreAdventure, R.id.genreMystery,
        R.id.genreCrime, R.id.genreFamily, R.id.genreMusical,
        R.id.genreWestern, R.id.genreWar
    )

    private val selectedGenres = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre_selection)

        // Set up click listeners for each genre block
        genreIds.forEach { id ->
            val genreBlock = findViewById<LinearLayout>(id)
            genreBlock?.let {
                val genreTag = it.tag as? String ?: ""
                            it.setOnClickListener { view ->
                                toggleGenreSelection(it, genreTag)
                            }
                        }
        }

        val isGuest = intent.getBooleanExtra("IS_GUEST", false)
        val continueBtn = findViewById<Button>(R.id.btnContinue)

        continueBtn.setOnClickListener {
            if (selectedGenres.size >= 3) {
                if (isGuest) {
                    // Save genres to SharedPreferences for guest
                    val sharedPreferences: SharedPreferences = getSharedPreferences("CineSelectGuestPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putStringSet("favorite_genres", selectedGenres)
                        apply()
                    }
                    Toast.makeText(this, "Guest genres saved!", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeActivity
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    homeIntent.putExtra("IS_GUEST", true)
                    startActivity(homeIntent)
                    finish()
                } else {
                    // Handle registered user
                    val selectedGenreIds = selectedGenres.map { genreNameToId(it) }
                    val preferences = UserPreferences(favoriteGenreIds = selectedGenreIds)
                    val apiService = ApiClient.getInstance(this)
                    val call = apiService.updateUserPreferences(preferences)

                    call.enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@GenreSelectionActivity, "Preferences saved!", Toast.LENGTH_SHORT).show()
                                val homeIntent = Intent(this@GenreSelectionActivity, HomeActivity::class.java)
                                startActivity(homeIntent)
                                finish()
                            } else {
                                Toast.makeText(this@GenreSelectionActivity, "Failed to save preferences. Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(this@GenreSelectionActivity, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            } else {
                Toast.makeText(this, "Pick at least 3 genres!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleGenreSelection(block: LinearLayout, tag: String) {
        if (selectedGenres.contains(tag)) {
            selectedGenres.remove(tag)
            setGenreSelected(block, false)
        } else {
            selectedGenres.add(tag)
            setGenreSelected(block, true)
        }
    }

    private fun setGenreSelected(block: LinearLayout, selected: Boolean) {
        val bg = GradientDrawable()
        bg.cornerRadius = resources.getDimension(R.dimen.genre_item_corner_radius)
        val colorRes = if (selected) R.color.coral else R.color.input_background
        bg.setColor(ContextCompat.getColor(this, colorRes))
        block.background = bg
    }

    private fun genreNameToId(name: String): Int {
        // This should be replaced with a more robust mapping, perhaps from a local DB or a network call
        return when (name.lowercase()) {
            "action" -> 28
            "adventure" -> 12
            "animation" -> 16
            "comedy" -> 35
            "crime" -> 80
            "documentary" -> 99
            "drama" -> 18
            "family" -> 10751
            "fantasy" -> 14
            "history" -> 36
            "horror" -> 27
            "music" -> 10402
            "mystery" -> 9648
            "romance" -> 10749
            "science fiction" -> 878
            "tv movie" -> 10770
            "thriller" -> 53
            "war" -> 10752
            "western" -> 37
            else -> 0
        }
    }
}