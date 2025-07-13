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

class SignUpPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)

        // initializing firebase authentication
        val auth = FirebaseAuth.getInstance()

        // initializing UI elements
        val signUpToLoginTextView = findViewById<TextView>(R.id.signUpToLoginTextView)
        val emailEditText = findViewById<EditText>(R.id.hoursEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val animation=findViewById<LottieAnimationView>(R.id.animation)


        animation.repeatCount= ValueAnimator.INFINITE
        animation.playAnimation()

        // set click listener to go to login page
        signUpToLoginTextView.setOnClickListener {

            //create intent to go to login page
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        //click listener for signup button
        signUpButton.setOnClickListener {

            //taking email and password from the user where he clicks on the signup button
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            //check if the user input is not empty and password matches confirm password
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword){

                // creating user account with email and password
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){       // if task is successful, direct the user to MainActivity.kt
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }.addOnFailureListener {        // if task unsuccessful, show error as toast
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
            else{       // if user inputs are empty, show toast
                Toast.makeText(this, "Enter all details correctly", Toast.LENGTH_SHORT).show()
            }
        }


    }
}