package com.saveetha.cineselect

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.saveetha.cineselect.R
import com.saveetha.cineselect.data.LoginResponse
import com.saveetha.cineselect.viewmodel.LoginNavigationState
import com.saveetha.cineselect.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        // Setup password visibility toggle
        btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible

            if (passwordVisible) {
                // Show password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_visibility_off)
            } else {
                // Hide password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_visibility)
            }

            // Move cursor to end of text
            etPassword.setSelection(etPassword.text.length)
        }

        // Handle back button click
        btnBack.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // FIX: Pass email and password as separate strings
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this@LoginActivity, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is LoginNavigationState.GoToHome -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                is LoginNavigationState.GoToGenres -> {
                    Toast.makeText(this, "Welcome! Please select your favorite genres.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, GenreSelectionActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                is LoginNavigationState.Error -> {
                    Toast.makeText(this, "Login error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}