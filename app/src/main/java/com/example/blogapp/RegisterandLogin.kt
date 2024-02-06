package com.example.blogapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivityRegisterandLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RegisterandLogin : AppCompatActivity() {
    private val binding:ActivityRegisterandLoginBinding by lazy {
        ActivityRegisterandLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST=1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initialize FirebaseAuthen
        auth = FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance("https://blogapp-897e0-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage=FirebaseStorage.getInstance()

        //for visibility
        val action = intent.getStringExtra("action")
        //adjust visible for login
        if(action=="login"){
            binding.loginButn.visibility= View.VISIBLE
            binding.loginEmail.visibility= View.VISIBLE
            binding.loginPassword.visibility= View.VISIBLE

            binding.cardView.visibility= View.GONE
            binding.registerName.visibility= View.GONE
            binding.registerEmail.visibility= View.GONE
            binding.registerPassword.visibility= View.GONE
            binding.registerButn.isEnabled=false;
            binding.registerButn.alpha=0.4f
            binding.registerNewhere.isEnabled=false
            binding.registerName.alpha=0.4f

            binding.loginButn.setOnClickListener {
                val loginEmail = binding.loginEmail.text.toString()
                val loginPassword = binding.loginPassword.text.toString()
                if(loginEmail.isEmpty() || loginPassword.isEmpty()){
                    Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(this, "Login Successful👍", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                            else{
                                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
        else if (action=="register"){
            binding.loginButn.isEnabled=false
            binding.loginButn.alpha=0.4f

            binding.registerButn.setOnClickListener {
                val regisName= binding.registerName.text.toString()
                val regisEmail= binding.registerEmail.text.toString()
                val regisPassword = binding.registerPassword.text.toString()

                if(regisName.isEmpty()|| regisEmail.isEmpty()|| regisPassword.isEmpty())
                {
                    Toast.makeText(this,"Please fill all details", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    auth.createUserWithEmailAndPassword(regisEmail, regisPassword)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                val user = auth.currentUser
                                auth.signOut()
                                user?.let {

                                    //Save user data in firebase realtime database
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(regisName, regisEmail)
                                    userReference.child(userId).setValue(userData)

                                    //upload image to firebase
                                    val storageRefernce= storage.reference.child("profile_image/$userId.jpg")
                                    storageRefernce.putFile(imageUri!!).addOnCompleteListener { task->
                                        if(task.isSuccessful){
                                            storageRefernce.downloadUrl.addOnCompleteListener { imageUri->
                                                if(imageUri.isSuccessful){
                                                    val imageUrl= imageUri.result.toString()

                                                    //save the image url to the realtime database
                                                    userReference.child(userId).child("profileImage").setValue(imageUrl)
                                                    /* Glide.with(this)
                                                         .load(imageUri)
                                                         .apply(RequestOptions.circleCropTransform())
                                                         .into(binding.registeruserImage)*/
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(this, "User Registration Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this,StartActivity::class.java))
                                    finish()
                                }
                            }else{
                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
        //setOnclickListner for choose image
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select image"),PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== RESULT_OK  && data!=null && data.data!=null)
            imageUri = data.data
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.registeruserImage)
    }
}

