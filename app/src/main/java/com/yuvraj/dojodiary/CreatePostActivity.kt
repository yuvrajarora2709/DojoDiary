package com.yuvraj.dojodiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)

        val auth = FirebaseAuth.getInstance()
        val database: FirebaseFirestore by lazy { Firebase.firestore }
        val USERID = auth.currentUser?.uid.toString()

        val organizationEditText = findViewById<EditText>(R.id.organizationEditText)
        val hoursEditText = findViewById<EditText>(R.id.hoursEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        //Get total hours data
        var totalHoursInNumber = 0
        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").get()
            .addOnSuccessListener {
                val totalHours = it.get("HOURS").toString()

                if (totalHours=="null"){
                    totalHoursInNumber = 0
                }
                else{
                    totalHoursInNumber = totalHours.toInt()
                }
                Toast.makeText(this, totalHoursInNumber.toString(), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()

            }

        //Creating new post
        saveButton.setOnClickListener {
            //Getting post data from the user input
            val hours = hoursEditText.text.toString()
            val organization = organizationEditText.text.toString()

            //To get current date and time
            val currentTime = LocalDateTime.now()
            //To get current day from the current date
            val dayOfWeek = currentTime.dayOfWeek.toString()

            //Create a date formatter
            val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd yyyy", Locale.ENGLISH)
            //Format the date
            val requiredDate = currentTime.format(dateFormatter).toString()

            //Create time formatter
            val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
            //Format the time
            val requiredTime = currentTime.format(timeFormatter)

            //Creating a random postid to save the post because we have not passed any name in the document
            val postId = database.collection("MY_DATABASE").document(USERID).collection("POST").document().id

            //Creating map before saving to database
            val map = mutableMapOf<String,String>()
            map.put("ORGANIZATION",organization)
            map.put("HOURS",hours)
            map.put("DAY",dayOfWeek)
            map.put("DATE",requiredDate)
            map.put("TIME",requiredTime)
            map.put("POST_ID",postId)

            //Saving the post to database

            database.collection("MY_DATABASE").document(USERID).collection("POST").document(postId).set(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()
                }

            //Update total hours

            val newTotalHours = totalHoursInNumber + hours.toInt()
            val map2 = mutableMapOf<String,String>()
            map2.put("HOURS",newTotalHours.toString())
            database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").set(map2)
                .addOnSuccessListener {
                    Toast.makeText(this, "Total hours updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Some error occured in updating total hours", Toast.LENGTH_SHORT).show()
                }
        }
    }
}