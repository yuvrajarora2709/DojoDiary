package com.yuvraj.dojodiary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.rpc.context.AttributeContext
import org.w3c.dom.Text

class RecyclerAdapter(private val activity: Activity, val arrayPost: ArrayList<PostDataModel>):RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database: FirebaseFirestore = Firebase.firestore
    private val USERID =  auth.currentUser?.uid.toString()

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val organizationTextView = itemView.findViewById<TextView>(R.id.organizationTextView)
        val hoursTextView = itemView.findViewById<TextView>(R.id.hoursTextView)
        val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        val dayTextView = itemView.findViewById<TextView>(R.id.dayTextView)
        val post_list_item = itemView.findViewById<ConstraintLayout>(R.id.post_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.MyViewHolder, position: Int) {
        holder.organizationTextView.text = arrayPost[position].organization
        holder.hoursTextView.text = arrayPost[position].hours
        holder.dateTextView.text = arrayPost[position].date
        holder.dayTextView.text = arrayPost[position].day

        holder.post_list_item.setOnLongClickListener {

            val builder = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete")
                .setIcon(R.drawable.delete)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes"){ dialogInterface, i ->

                    try {

                        deleteHoursOnServer(position, holder.itemView.context)
                        updateTotalHoursAfterDeleting(holder.itemView.context, arrayPost[position].hours!!,position)

                    } catch (e:Exception){
                        Toast.makeText(holder.itemView.context, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No"){ dialogInterface, i->
                    Toast.makeText(holder.itemView.context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                }
            builder.show()




            true
        }
    }

    override fun getItemCount(): Int {
        return arrayPost.size
    }

    fun deleteHoursOnServer(position: Int,context:Context){

        val postID = arrayPost[position].postId
        database.collection("MY_DATABASE").document(USERID).collection("POST").document(postID!!).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Some error occurred in deleting post", Toast.LENGTH_SHORT).show()
            }
    }


    fun updateTotalHoursAfterDeleting(context: Context,hours:String,position: Int){

        var totalHoursInNumber = 0
        database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").get()
            .addOnSuccessListener {

                val totalHours = it.get("HOURS").toString()

                if (totalHours!="null"){
                    totalHoursInNumber = totalHours.toInt()
                }
                else{
                    totalHoursInNumber = 0
                }

                //Updated hours
                val updatedHours = totalHoursInNumber-hours.toInt()

                val map = mutableMapOf<String,String>()
                map.put("HOURS",updatedHours.toString())
                database.collection("MY_DATABASE").document(USERID).collection("PROFILE").document("TOTAL_HOURS").set(map)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Total hours updated", Toast.LENGTH_SHORT).show()
                        arrayPost.removeAt(position)
                        notifyItemRemoved(position)

                        val intent = Intent(activity,MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Couldn't update hours", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Couldn't fetch total hours", Toast.LENGTH_SHORT).show()
            }
    }
}