package com.example.sikyann.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(auth.currentUser!=null){
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
                else{
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        ,1000)

    }
}
