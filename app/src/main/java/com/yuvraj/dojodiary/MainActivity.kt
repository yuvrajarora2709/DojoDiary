package com.yuvraj.dojodiary

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class  MainActivity : AppCompatActivity() {

    lateinit var arrayPost: ArrayList<PostDataModel>
    val auth = FirebaseAuth.getInstance()
    val database: FirebaseFirestore by lazy { Firebase.firestore }
    val USERID = auth.currentUser?.uid.toString()
    lateinit var recyclerView: RecyclerView
    lateinit var totalHoursTextView: TextView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initalizaing firebase auth
        val auth = FirebaseAuth.getInstance()
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        totalHoursTextView = findViewById<TextView>(R.id.totalHoursTextView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility= View.VISIBLE

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        arrayPost = ArrayList()
        val recyclerAdapter = RecyclerAdapter(this, arrayPost)
        recyclerView.adapter = recyclerAdapter


        val createPostFAB = findViewById<FloatingActionButton>(R.id.createPostFAB)

        //checks if the user is signed in or not
        if (auth.currentUser==null){
            // if the user is not signed in, send him to login page
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        profileImageView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        createPostFAB.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        getPostData()
        fetchTotalHours()
    }

    private fun getPostData(){

        database.collection("MY_DATABASE").document(USERID).collection("POST").orderBy("TIME", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result->
                arrayPost.clear()

                for (document in result.documents){
                    val organizationData = document.getString("ORGANIZATION")?:""
                    val hoursData = document.getString("HOURS")?:""
                    val dayData = document.getString("DAY")?:""
                    val dateData = document.getString("DATE")?:""
                    val timeData = document.getString("TIME")?:""
                    val postIdData = document.getString("POST_ID")?:""

                    arrayPost.add(PostDataModel(organizationData,hoursData,dayData,dateData,timeData,postIdData))
                }
                recyclerView.adapter?.notifyDataSetChanged()
                progressBar.visibility=View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchTotalHours(){
        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").get()
            .addOnSuccessListener {
                val totalMinutes = it.get("HOURS").toString()
                val totalMinutesInNumber = totalMinutes?.toIntOrNull()?:0
                val hours = totalMinutesInNumber/60
                val minutes = totalMinutesInNumber%60
                var time = ""
                if (minutes==0){
                    time = "$hours"
                }
                else {
                    time = "$hours:$minutes"
                }


                if (totalMinutes=="null"){
                    totalHoursTextView.text = "Total Hours: 0"
                }
                else{
                    totalHoursTextView.text = "Total Hours: $time"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't fetch total hours", Toast.LENGTH_SHORT).show()
            }
    }
}