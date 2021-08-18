package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_student_register.*

class StudentActivityRegister : RootActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        if(!isNetworkActive(this@StudentActivityRegister)){
            showSnackbar(getString(R.string.network),student_register_view)
            student_register_view.visibility = View.GONE
        }else{

            student_registration_submit.setOnClickListener {
                if(auth.currentUser==null){
                    if(validate()){
                        showProgress()
                        auth.createUserWithEmailAndPassword(student_gmail.text.toString(),student_password.text.toString())
                            .addOnCompleteListener(this@StudentActivityRegister) { task ->
                                if(task.isSuccessful){
                                    Log.d(Constants.TAG, "createUserWithEmail:success")
                                    val user = auth.currentUser
                                    val details: UserDetails =
                                        UserDetails(student_name.text.toString(),student_father.text.toString(),student_gmail.text.toString(),
                                            student_address.text.toString(),student_mobile.text.toString(),Constants.STUDENT)
                                    db.collection(Constants.USER).document(user!!.uid).set(details)
                                    hideProgress()
                                    startActivity(Intent(this@StudentActivityRegister,LoginActivity::class.java))
                                    finish()
                                }else {
                                    hideProgress()
                                    Log.d(Constants.TAG, task.exception.toString())
                                    if(task.exception.toString().contains("FirebaseAuthUserCollisionException")){
                                        Toast.makeText(baseContext, "Already exist an account",
                                            Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(baseContext, "Account creation failed.",
                                            Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }
                    }
                }
            }
        }

    }

    private fun validate():Boolean{
        if(student_name.text.toString().length<6){
            Toast.makeText(this@StudentActivityRegister,"Enter Name Properly", Toast.LENGTH_SHORT).show()
            return false
        }
        if(student_father.text.toString().length<6){
            Toast.makeText(this@StudentActivityRegister,"Enter Father's Name Properly", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(student_gmail.text.toString()).matches()){
            Toast.makeText(this@StudentActivityRegister,"Enter gmail Properly", Toast.LENGTH_SHORT).show()
            return false
        }
        if(student_address.text.toString().isEmpty()){
            Toast.makeText(this@StudentActivityRegister,"Enter Address Properly", Toast.LENGTH_SHORT).show()
            return false
        }
        if(student_mobile.text.toString().length!=10){
            Toast.makeText(this@StudentActivityRegister,"Enter Mobile Number Properly", Toast.LENGTH_SHORT).show()
            return false
        }

        if(student_password.text.toString().length!=8){
            Toast.makeText(this@StudentActivityRegister,"password length must be 8", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
