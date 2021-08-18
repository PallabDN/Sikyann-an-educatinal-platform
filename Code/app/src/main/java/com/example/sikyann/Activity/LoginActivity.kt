package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : RootActivity() {
    val auth = FirebaseAuth.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(!isNetworkActive(this@LoginActivity)){
            showSnackbar(getString(R.string.network),log_in_view)
            log_in_view.visibility = View.GONE
        }else{
            login_student.setOnClickListener {
                if(auth.currentUser==null){
                    showProgress()
                    auth.signInWithEmailAndPassword(user_gmail.text.toString(),user_password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(Constants.TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                hideProgress()
                                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                                finish()

                            } else {
                                hideProgress()
                                // If sign in fails, display a message to the user.
                                Log.d(Constants.TAG,task.exception.toString())
                                if(task.exception.toString().contains("FirebaseAuthInvalidCredentialsException")){
                                    Toast.makeText(baseContext, "Invalid password",
                                        Toast.LENGTH_SHORT).show()
                                }else if(task.exception.toString().contains("FirebaseAuthInvalidUserException")){
                                    Toast.makeText(baseContext, "There is no user",
                                        Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(baseContext, "LogIn failed",
                                        Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                }
                else{
                    startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                    finish()
                }
            }

            user_forgot_password.setOnClickListener {
                if(!isNetworkActive(this@LoginActivity)){
                    showSnackbar(getString(R.string.network),log_in_view)
                }else{
                    val gmail = user_gmail.text.toString()
                    if(!Patterns.EMAIL_ADDRESS.matcher(gmail).matches()){
                        Toast.makeText(baseContext, "Enter Gmail",
                            Toast.LENGTH_SHORT).show()
                    }
                    else{
                        auth.sendPasswordResetEmail(gmail)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(Constants.TAG, "Email sent.")
                                    Toast.makeText(baseContext, "Password recovery link has sent to your Email Address",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

            }

            register.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this@LoginActivity,"please Log in",Toast.LENGTH_SHORT).show()
    }
}
