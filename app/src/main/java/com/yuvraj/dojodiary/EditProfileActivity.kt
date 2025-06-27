package com.yuvraj.dojodiary

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

class EditProfileActivity : AppCompatActivity() {

    // Initializaing firebase authentication, database, and USERID
    val auth = FirebaseAuth.getInstance()
    val database: FirebaseFirestore by lazy { Firebase.firestore }
    val USERID = auth.currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Initialization of UI elements
        val saveButton = findViewById<Button>(R.id.saveButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)

        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("NAME").get()
            .addOnSuccessListener {
                val fetchedName = it.get("NAME").toString()
                nameEditText.setText(fetchedName)

                if (fetchedName=="null"){
                    nameEditText.setText("No Name")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }


        // Set click listener for name edittext
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()

            // if name is not empty, display map
            if (name.isNotEmpty()){
                val map = mutableMapOf<String,String>()
                map.put("NAME",name)

                // added database collection to store items and if task is successful, display changes saved and when task fails, display some error occured
                database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("NAME").set(map)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }

            }
            // if user inputs are empty, show toast
            else{
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }
}