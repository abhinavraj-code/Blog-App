package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blogapp.databinding.ActivityStartactivityBinding
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    val binding:ActivityStartactivityBinding by lazy {
        ActivityStartactivityBinding.inflate(layoutInflater)
    }
    private  lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        binding.loginBtn.setOnClickListener {
            val intent= Intent(this,RegisterandLogin::class.java)
            intent.putExtra("action","login")
            startActivity(intent)
            finish()
        }
        binding.registerBtn.setOnClickListener {
            val intent= Intent(this,RegisterandLogin::class.java)
            intent.putExtra("action","register")
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentu= auth.currentUser
        if(currentu!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}