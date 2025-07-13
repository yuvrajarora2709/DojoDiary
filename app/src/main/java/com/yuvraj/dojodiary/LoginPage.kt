package com.yuvraj.dojodiary

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        // initializing firebase authentication
        val auth = FirebaseAuth.getInstance()

        // initalizing UI elements
        val loginToSignUpTextView = findViewById<TextView>(R.id.loginToSignUpTextView)
        val emailEditText = findViewById<EditText>(R.id.hoursEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val animation=findViewById<LottieAnimationView>(R.id.animation)


        animation.repeatCount=ValueAnimator.INFINITE
        animation.playAnimation()

        // set click listener to navigate to signup page
        loginToSignUpTextView.setOnClickListener {

            // create intent to navigate to signup page
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
            finish()
        }

        // click listener for login button
        loginButton.setOnClickListener {

            // taking email and password from the user
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // check if the user input is not empty
            if (email.isNotEmpty() && password.isNotEmpty()){

                // signing in the account with email and password
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){       // if task is successful, direct user to MainActivity.kt
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }.addOnFailureListener {        // if task is unsuccessful, display localized message
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }

            }
            else {      //if user inputs are empty, show toast
                Toast.makeText(this, "Enter all details correctly", Toast.LENGTH_SHORT).show()
            }
        }





    }
}