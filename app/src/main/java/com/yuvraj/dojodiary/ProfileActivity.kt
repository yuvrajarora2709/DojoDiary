package com.yuvraj.dojodiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.jar.Attributes.Name

class ProfileActivity : AppCompatActivity() {

    // Initializing firebase authentication, database, and USERID
    val auth = FirebaseAuth.getInstance()
    val database: FirebaseFirestore by lazy { Firebase.firestore }
    val USERID = auth.currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Initialization of UI Elements
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val hoursTextView = findViewById<TextView>(R.id.hoursTextView)
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val refreshFAB = findViewById<FloatingActionButton>(R.id.refreshFAB)

        refreshFAB.setOnClickListener{
            // Added Database to sore profile and name
            database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("NAME").get()

                // added successlistener to fetch/keep name for profile
                .addOnSuccessListener {
                    val fetchedName = it.get("NAME").toString()
                    nameTextView.setText("Name: "+fetchedName)
                    //Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()

                    if (fetchedName=="null"){
                        nameTextView.setText("Name: None")
                    }
                }

            database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").get()

                .addOnSuccessListener {
                    val fetchedHours = it.get("HOURS").toString()
                    hoursTextView.setText("Total Hours: "+fetchedHours)

                    if (fetchedHours=="null"){
                        hoursTextView.setText("Total Hours: 0")
                    }
                }

        }

        // Added Database to sore profile and name
        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("NAME").get()

            // added successlistener to fetch/keep name for profile
            .addOnSuccessListener {
                val fetchedName = it.get("NAME").toString()
                nameTextView.setText("Name: "+fetchedName)
                //Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()

                if (fetchedName=="null"){
                    nameTextView.setText("Name: None")
                }
            }

        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").get()

            .addOnSuccessListener {
                val fetchedHours = it.get("HOURS").toString()
                hoursTextView.setText("Total Hours: "+fetchedHours)

                if (fetchedHours=="null"){
                    hoursTextView.setText("Total Hours: 0")
                }
            }

        // set clicklistener for logout
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        // set clicklistener for edit profile
        editProfileButton.setOnClickListener {
            val intent = Intent(this,EditProfileActivity::class.java)
            startActivity(intent)
        }




    }
}