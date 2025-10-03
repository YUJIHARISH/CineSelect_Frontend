package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

// Note: Changed back to AppCompatActivity for consistency with your other activities.
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This sets the UI from a layout file in res/layout/activity_splash.xml
        setContentView(R.layout.activity_splash)

        // Hide the action bar for a clean splash screen
        supportActionBar?.hide()

        // Handler to delay the start of the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Intent to start WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            // Finish this activity so the user can't navigate back to it
            finish()
        }, SPLASH_DELAY)
    }
}