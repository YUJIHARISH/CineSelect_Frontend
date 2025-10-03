package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.saveetha.cineselect.viewmodel.SignupViewModel
import com.saveetha.cineselect.network.AuthTokenManager
import com.saveetha.cineselect.network.NetworkResult
import android.util.Log

class SignupActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var btnBack: ImageButton
    private lateinit var tvCreateAccount: TextView
    private lateinit var tilFullName: TextInputLayout
    private lateinit var etFullName: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: AppCompatButton
    private lateinit var tvTerms: TextView
    private lateinit var tvLogin: TextView

    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI elements
        initializeViews()

        // Set up click listeners
        setupClickListeners()

        signupViewModel.signupResult.observe(this, Observer { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Toast.makeText(this, "Signup successful! Please log in.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                is NetworkResult.Error -> {
                    Toast.makeText(this, "Signup failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
        tilFullName = findViewById(R.id.tilFullName)
        etFullName = findViewById(R.id.etFullName)
        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvTerms = findViewById(R.id.tvTerms)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setupClickListeners() {
        // Back button - returns to previous screen
        btnBack.setOnClickListener {
            finish()
        }

        // Sign up button
        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                // Implement your signup logic here
                performSignUp()
            }
        }

        // Login text - navigate to login screen
        tvLogin.setOnClickListener {
            // Navigate to login activity
            // Example: startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close current activity after navigation
        }

        // Terms and conditions text
        tvTerms.setOnClickListener {
            // Show terms and conditions
            // Example: startActivity(Intent(this, TermsActivity::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate full name
        if (etFullName.text.toString().trim().isEmpty()) {
            tilFullName.error = "Please enter your full name"
            isValid = false
        } else {
            tilFullName.error = null
        }

        // Validate email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = "Please enter your email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            isValid = false
        } else {
            tilEmail.error = null
        }

        // Validate password
        val password = etPassword.text.toString()
        if (password.isEmpty()) {
            tilPassword.error = "Please enter a password"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            tilPassword.error = null
        }

        // Validate confirm password
        val confirmPassword = etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            tilConfirmPassword.error = null
        }

        return isValid
    }

    private fun performSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        signupViewModel.signup(fullName, email, password)
    }
}