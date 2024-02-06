package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Adapter.AdapterBlog
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.databinding.ActivitySavedarticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedarticleActivity : AppCompatActivity() {

    private val binding: ActivitySavedarticleBinding by  lazy {
        ActivitySavedarticleBinding.inflate(layoutInflater)
    }
    private  val savedBlogArticles = mutableListOf<ItemsBlogModel>()
    private lateinit var blogAdapter : AdapterBlog
    private val auth =FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //initialize AdapterBlog
        blogAdapter= AdapterBlog(savedBlogArticles.filter { it.isSaved }.toMutableList())

        val recyclerview = binding.savedArticleRecyclerview
        recyclerview.adapter= blogAdapter
        recyclerview.layoutManager= LinearLayoutManager(this)

        val userId = auth.currentUser?.uid
        if(userId!=null){
            val userReference = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(userId).child("savePosts")

            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSanpshot in snapshot.children){
                        val postId = postSanpshot.key
                        val isSaved = postSanpshot.value as Boolean
                        if (postId!=null && isSaved){
                            //Fetch the corresponding blog item on postId using a coroutine
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem!=null){
                                    savedBlogArticles.add(blogItem)

                                    launch (Dispatchers.Main){
                                        blogAdapter.updateData(savedBlogArticles)
                                    }
                                }
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun fetchBlogItem(postId: String): ItemsBlogModel? {
        val  blogRefrence = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("blogs")
        return  try {
            val dataSnapshot = blogRefrence.child(postId).get().await()
            val blogData = dataSnapshot.getValue(ItemsBlogModel::class.java)
            blogData
        }catch (e:Exception){
            null
        }
    }
}