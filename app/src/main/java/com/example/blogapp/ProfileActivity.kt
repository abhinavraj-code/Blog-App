package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private  val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //goto add article layout
        binding.addNewarticleButton.setOnClickListener {
            startActivity(Intent(this,ArticleAddActivity::class.java))
        }

        //goto your article layout
        binding.yourArticlebutton.setOnClickListener {
            startActivity(Intent(this, YourrticleActivity::class.java))
        }

        //goto start layout
        binding.logOut.setOnClickListener {
            //for logout
            auth.signOut()
            //navigate
            startActivity(Intent(this,StartActivity::class.java))
            finish()
        }

        //initialize firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference.child("users")
        val userId =auth.currentUser?.uid
        if (userId!=null)
        {
            loadUserprofileData(userId)
        }
    }

    private fun loadUserprofileData(userId: String) {
        val  userReference = databaseReference.child(userId)
        //load user profile image
        userReference.child("profileImage").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl!=null)
                {
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .into(binding.userProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load Pofile Image 😔", Toast.LENGTH_SHORT).show()
            }
        })

        // load user Name
        userReference.child("name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               val userName = snapshot.getValue(String::class.java)
                if(userName!=null){
                    binding.userName.text = userName
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}