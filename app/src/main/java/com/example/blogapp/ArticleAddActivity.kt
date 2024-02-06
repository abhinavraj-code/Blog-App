package com.example.blogapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivityArticleAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

class ArticleAddActivity : AppCompatActivity() {
    private val binding: ActivityArticleAddBinding by lazy {
        ActivityArticleAddBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    private val auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imageButton.setOnClickListener{
            finish()
        }

        binding.addblogbutton.setOnClickListener {

            val title= binding.blogtitle.editText?.text.toString().trim()
            val description = binding.blogdescription.editText?.text?.toString()?.trim()

            if (description != null) {
                if(title.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(this, "This fill all fields", Toast.LENGTH_SHORT).show()

                }
                //get currentuser
                val user:FirebaseUser?=auth.currentUser

                if(user!=null){
                    val userId=user.uid
                    val userName:String=user.displayName?:"Anonymous"
                    val userImageUrl=user.photoUrl?:""

                    // fetch user name and userprofile from database

                    userReference.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            if(userData!=null){
                                val userNameFromDB =userData.name
                                val userImageUrlFromDB= userData.profileImage

                                val currentDate =SimpleDateFormat("yyyy-MM-dd").format(Date())

                                //create a blogitem Model
                                val blogItem= ItemsBlogModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB
                                )
                                //generate a unique  kety for blog post

                                val key=databaseReference.push().key
                                if(key!=null)
                                {
                                    blogItem.postId=key
                                    val blogRefernece= databaseReference.child(key)
                                    blogRefernece.setValue(blogItem).addOnCompleteListener {
                                        if(it.isSuccessful)
                                        {
                                            finish()
                                        }else{
                                            Toast.makeText(this@ArticleAddActivity, "Failed to add blog", Toast.LENGTH_SHORT).show()
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

            }
        }
    }
}