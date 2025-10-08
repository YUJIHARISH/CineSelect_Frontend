package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saveetha.cineselect.data.User
import com.saveetha.cineselect.network.ApiClient
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnEditProfile: Button
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var tvUserName: TextView
    private lateinit var tvFavoriteGenres: TextView
    private lateinit var tvWatchlistCount: TextView

    // Preference items
    private lateinit var prefLanguage: LinearLayout
    private lateinit var prefTheme: LinearLayout
    private lateinit var prefContentFilters: LinearLayout
    private lateinit var prefPrivacy: LinearLayout
    private lateinit var prefAccount: LinearLayout
    private lateinit var prefNotifications: LinearLayout
    private lateinit var prefHelp: LinearLayout
    private lateinit var prefAbout: LinearLayout
    private lateinit var prefLogOut: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        loadUserData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        NavigationHelper.navigateBack(this)
    }

    private fun initializeViews() {
        btnEditProfile = findViewById(R.id.btnEditProfile)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        tvUserName = findViewById(R.id.tvUserName)
        tvFavoriteGenres = findViewById(R.id.tvFavoriteGenres)
        tvWatchlistCount = findViewById(R.id.tvWatchlistCount)

        // Initialize preference items
        prefLanguage = findViewById(R.id.prefLanguage)
        prefTheme = findViewById(R.id.prefTheme)
        prefContentFilters = findViewById(R.id.prefContentFilters)
        prefPrivacy = findViewById(R.id.prefPrivacy)
        prefAccount = findViewById(R.id.prefAccount)
        prefNotifications = findViewById(R.id.prefNotifications)
        prefHelp = findViewById(R.id.prefHelp)
        prefAbout = findViewById(R.id.prefAbout)
        prefLogOut = findViewById(R.id.prefLogOut)
    }

    private fun setupClickListeners() {
        // Edit Profile button
        btnEditProfile.setOnClickListener {
            editProfile()
        }

        // Preference item clicks
        prefLanguage.setOnClickListener {
            openLanguageSettings()
        }

        prefTheme.setOnClickListener {
            openThemeSettings()
        }

        prefContentFilters.setOnClickListener {
            openContentFilters()
        }

        prefPrivacy.setOnClickListener {
            openPrivacySettings()
        }

        prefAccount.setOnClickListener {
            openAccountSettings()
        }

        prefNotifications.setOnClickListener {
            openNotificationSettings()
        }

        prefHelp.setOnClickListener {
            openHelp()
        }

        prefAbout.setOnClickListener {
            openAbout()
        }

        prefLogOut.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setupBottomNavigation() {
        // Set the profile tab as selected
        bottomNavigation.selectedItemId = R.id.navigation_profile

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
                R.id.navigation_watchlist -> {
                    NavigationHelper.navigateTo(this, MyWatchlistActivity::class.java)
                    true
                }
                R.id.navigation_profile -> {
                    // Already on profile screen
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val user = ApiClient.getInstance(this@ProfileActivity).getMyProfile()
                tvUserName.text = user.fullName
                val favoriteGenresText = user.favoriteGenres?.joinToString(separator = ", ") { it.name }
                tvFavoriteGenres.text = favoriteGenresText ?: "None selected"
                tvWatchlistCount.text = user.watchlistCount.toString()
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Failed to load profile", e)
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editProfile() {
        Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        // Navigate to EditProfileActivity
        // val intent = Intent(this, EditProfileActivity::class.java)
        // startActivity(intent)
    }

    private fun openLanguageSettings() {
        Toast.makeText(this, "Language Settings clicked", Toast.LENGTH_SHORT).show()
        // Navigate to LanguageSettingsActivity
    }

    private fun openThemeSettings() {
        Toast.makeText(this, "Theme Settings clicked", Toast.LENGTH_SHORT).show()
        // Navigate to ThemeSettingsActivity
    }

    private fun openContentFilters() {
        Toast.makeText(this, "Content Filters clicked", Toast.LENGTH_SHORT).show()
        // Navigate to ContentFiltersActivity
    }

    private fun openPrivacySettings() {
        Toast.makeText(this, "Privacy Settings clicked", Toast.LENGTH_SHORT).show()
        // Navigate to PrivacySettingsActivity
    }

    private fun openAccountSettings() {
        Toast.makeText(this, "Account Settings clicked", Toast.LENGTH_SHORT).show()
        // Navigate to AccountSettingsActivity
    }

    private fun openNotificationSettings() {
        Toast.makeText(this, "Notification Settings clicked", Toast.LENGTH_SHORT).show()
        // Navigate to NotificationSettingsActivity
    }

    private fun openHelp() {
        Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show()
        // Navigate to HelpActivity or show help dialog
    }

    private fun openAbout() {
        Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
        // Navigate to AboutActivity or show about dialog
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Clear user data and navigate to login screen
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
        
        // Clear SharedPreferences or other user data
        // val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        // sharedPrefs.edit().clear().apply()
        
        // Navigate to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_USER_NAME = "USER_NAME"
        const val EXTRA_USER_EMAIL = "USER_EMAIL"
    }
}
