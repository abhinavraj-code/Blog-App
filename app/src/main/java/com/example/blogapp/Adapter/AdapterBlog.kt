package com.example.blogapp.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.ReadmoreActivity
import com.example.blogapp.databinding.ItemsBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.blogapp.R
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY

class AdapterBlog(private  val items: MutableList<ItemsBlogModel>):
    RecyclerView.Adapter <AdapterBlog.BlogViewHolder>() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val currentUser =FirebaseAuth.getInstance().currentUser

  inner class BlogViewHolder(private  val  binding: ItemsBlogBinding) : RecyclerView.ViewHolder(binding.root){
      fun bind(itemsBlogModel: ItemsBlogModel) {
          val postId = itemsBlogModel.postId
          val context =binding.root.context

          binding.heading.text= itemsBlogModel.heading
          Glide.with(binding.profile.context)
              .load(itemsBlogModel.profileImage)
              .into(binding.profile)
          binding.username.text=itemsBlogModel.userName
          binding.date.text=itemsBlogModel.date
          binding.post.text= itemsBlogModel.post
         // binding.likeCount.text= itemsBlogModel.likeCount.toString()

          //setOnClickListerner
          binding.root.setOnClickListener {
              val context = binding.root.context
              val intent = Intent(context,ReadmoreActivity::class.java)
              intent.putExtra("blogItem",itemsBlogModel)
              context.startActivity(intent)
          }
          //check if the currentuser liked post and update like btn

        /* val postLikeReference =databaseReference.child("blogs").child("likes")
          val currentUserLiked =currentUser?.uid?.let {  uid->
              postLikeReference.child(uid).addListenerForSingleValueEvent(object :ValueEventListener{

                  override fun onDataChange(snapshot: DataSnapshot) {
                      if(snapshot.exists()){
                          binding.likebutton.setImageResource(R.drawable.heart_red)
                      }else{
                          binding.likebutton.setImageResource(R.drawable.heart_blackoutp)
                      }
                  }
                  override fun onCancelled(error: DatabaseError) {

                  }
              })
          }*/

                    //handle like button
                   /* binding.likebutton.setOnClickListener{
                    if (currentUser!=null){
                        handleLikeButtonClick(postId,itemsBlogModel,binding)
                    }else{
                        Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
                    }
                }*/

          //set icon based on saved status
          val userReference = databaseReference.child("users").child(currentUser?.uid?:"")
          val postSaveReference = userReference.child("savePosts").child(postId!!)

          postSaveReference.addListenerForSingleValueEvent(object : ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  //if blog already saved
                  if (snapshot.exists()){
                    binding.postsaveButton.setImageResource(R.drawable.save_red)
                  }else
                  {
                      //not saved
                      binding.postsaveButton.setImageResource(R.drawable.save_redout)
                  }
              }
              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }
          })

          //handle save button
            binding.postsaveButton.setOnClickListener{
             if (currentUser!=null){
                 handleSaveButtonClick(postId,itemsBlogModel,binding)
             }else{
               Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
             }
            }
      }
   }

    private fun handleSaveButtonClick(postId: String?, itemsBlogModel: ItemsBlogModel, binding: ItemsBlogBinding) {
        val userReference =databaseReference.child("users").child(currentUser!!.uid)
        userReference.child("savePosts").child(postId!!).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //blog unsave feature
                    userReference.child("savePosts").child(postId).removeValue()
                        .addOnSuccessListener {
                            //update UI
                            val clickedItemBlog = items.find{it.postId==postId }
                            clickedItemBlog?.isSaved = false
                            notifyDataSetChanged()

                            val context= binding.root.context
                            Toast.makeText(context, "Blog Unsaved!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            val context= binding.root.context
                            Toast.makeText(context, "Failed to unsave Blog", Toast.LENGTH_SHORT).show()
                        }
                    //change save icon
                    binding.postsaveButton.setImageResource(R.drawable.save_redout)
                }
                else{
                    //save blog
                    userReference.child("savePosts").child(postId).setValue(true)
                        .addOnSuccessListener {
                            //Update UI
                            val clickedItemBlog=items.find { it.postId==postId }
                            clickedItemBlog?.isSaved=true
                            notifyDataSetChanged()

                            val context= binding.root.context
                            Toast.makeText(context, "Blog Saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            val context= binding.root.context
                            Toast.makeText(context, "Failed to save Blog", Toast.LENGTH_SHORT).show()
                        }
                    //change save icon
                    binding.postsaveButton.setImageResource(R.drawable.save_red)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    /* private fun handleLikeButtonClick(postId: String?, itemsBlogModel: ItemsBlogModel,binding: ItemsBlogBinding) {
         val userReference = databaseReference.child("users").child(currentUser!!.uid)
         val postLikeReference = databaseReference.child("blogs").child(postId!!).child("likes")
 
         //unlike it if you already like post
 
         postLikeReference.child(currentUser.uid).addListenerForSingleValueEvent(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists())
                 {
                     if (postId != null) {
                         userReference.child("likes").child(postId).removeValue()
                              .addOnSuccessListener {
                             postLikeReference.child(currentUser.uid).removeValue()
                             itemsBlogModel.likeBy?.remove(currentUser.uid)
                             updateLikeButton( binding ,false)
 
                             //decreament like in database
                             val newLikeCount = itemsBlogModel.likeCount-1
                             itemsBlogModel.likeCount=newLikeCount
                             databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                             notifyDataSetChanged()
                         }
                             .addOnFailureListener { e->
                                 Log.e("LikedClicked", "onDataChange: Failed to unlike $e", )
                             }
                     }
                     else{
                         //user not liked the post ,so like it
                         if (postId != null) {
                             userReference.child("likes").child(postId).setValue(true)
                                 .addOnSuccessListener {
                                     postLikeReference.child(currentUser.uid).setValue(true)
                                     itemsBlogModel.likeBy?.add(currentUser.uid)
                                     updateLikeButton(binding,true)
 
                                     //Increament like count
                                     val newLikeCount = itemsBlogModel.likeCount+1
                                     itemsBlogModel.likeCount=newLikeCount
                                     databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                                     notifyDataSetChanged()
                                 }
                                 .addOnFailureListener { e->
                                     Log.e("LikedClicked", "onDataChange: Failed to like $e", )
                                 }
                         }
                     }
                 }
             }
 
             override fun onCancelled(error: DatabaseError) {

             }
 
         })
     }*/

   /* private fun updateLikeButton(binding: ItemsBlogBinding ,liked: Boolean) {
        if(liked){
            binding.likebutton.setImageResource(R.drawable.heart_blackoutp)
        }
        else
        {
            binding.likebutton.setImageResource(R.drawable.heart_red)
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemsBlogBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    fun updateData(savedBlogArticles: List<ItemsBlogModel>) {

        items.clear()
        items.addAll(savedBlogArticles)
        notifyDataSetChanged()
    }

    fun setfilteredlist(filteredlist: MutableList<ItemsBlogModel>) {
       val filteredlist= items
        notifyDataSetChanged()
    }
}