package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.databinding.ActivityArticleAddBinding
import com.example.blogapp.databinding.ActivityReadmoreBinding

class ReadmoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadmoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityReadmoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        
        val blogs = intent.getParcelableExtra<ItemsBlogModel>("blogItem")
        if(blogs!=null){
            //Retrive user data e.g. z blog title
            binding.titleText.text= blogs.heading
            binding.userName.text= blogs.userName
            binding.date.text= blogs.date
            binding.blogdescriptionTextview.text= blogs.post

            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)
        }
        else{
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
        }
    }
}