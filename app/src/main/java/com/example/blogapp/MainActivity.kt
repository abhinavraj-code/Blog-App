package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.Adapter.AdapterBlog
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private  lateinit var databaseReference: DatabaseReference
    private  val blogitems= mutableListOf<ItemsBlogModel>()
    private lateinit var auth: FirebaseAuth
    private lateinit var  adapterBlog: AdapterBlog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //searchview
        val searchview = binding.searchBlog

        searchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterlist(newText)
                return true
            }
        })



        //to go saved article layout
        binding.saveArticleButton.setOnClickListener {
            val intent= Intent(this,SavedarticleActivity::class.java)
            startActivity(intent)
        }
        //to go profile layout
        binding.profileImage.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        binding.cardView2.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        //to go profile layout
        auth = FirebaseAuth.getInstance()
        databaseReference =FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")

        val userId = auth.currentUser?.uid

        //set user profile
        if(userId!=null){
            loadProfileImage(userId)
        }

        //set blog into recyclerview
        //initialize
        val recyclerView = binding.recyclerBlog
        val blogadapter = AdapterBlog(blogitems)
        recyclerView.adapter = blogadapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fetch data from database
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogitems.clear()
                for (snapshot in snapshot.children)
                {
                    val blogItem = snapshot.getValue(ItemsBlogModel::class.java)
                    if(blogItem!=null)
                    {
                        blogitems.add(blogItem)
                    }
                }
                //reverse data list  to show new data on top
                blogitems.reverse()
                //Notify the adapter when data is changed
                blogadapter.notifyDataSetChanged()

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding.floatingaddarticleButton.setOnClickListener {
            startActivity(Intent(this,ArticleAddActivity::class.java))
        }
    }

    private fun filterlist(newText: String?) {
        if(newText!=null){
            val filteredlist= mutableListOf<ItemsBlogModel>()
            for(i in blogitems){
                if (i.heading!!.toLowerCase(Locale.ROOT).contains(newText)){
                    filteredlist.add(i)
                }
            }
            if(filteredlist.isEmpty()){
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            }
            else{
                adapterBlog.setfilteredlist(filteredlist)
            }
        }
    }


    private fun loadProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userId)
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)

                if(profileImageUrl!=null)
                {
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,  "Error loading profile image", Toast.LENGTH_SHORT).show()
            }
        })
    }
}