package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.cineselect.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Hide action bar if needed
        supportActionBar?.hide()
    }

    private fun setupClickListeners() {
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val tvContinueAsGuest: TextView = findViewById(R.id.tvContinueAsGuest)

        btnLogin.setOnClickListener {
            // Navigate to Login Activity
            navigateToLogin()
        }

        btnSignUp.setOnClickListener {
            // Navigate to Sign Up Activity
            navigateToSignUp()
        }

        tvContinueAsGuest.setOnClickListener {
            // Navigate to Main App as Guest
            navigateToMainAsGuest()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSignUp() {
        // Implement navigation to Sign Up screen
        // Example:
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainAsGuest() {
        val intent = Intent(this, GenreSelectionActivity::class.java)
        intent.putExtra("IS_GUEST", true)
        startActivity(intent)
    }
}