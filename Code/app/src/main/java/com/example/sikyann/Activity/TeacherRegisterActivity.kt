package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_teacher_register.*

class TeacherRegisterActivity : RootActivity() {



    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_register)

        if(!isNetworkActive(this@TeacherRegisterActivity)){
            showSnackbar(getString(R.string.network),teacher_register_view)
            teacher_register_view.visibility = View.GONE
        }else{
            val currentUser = auth.currentUser

            teacher_registration_submit.setOnClickListener {
                if(currentUser==null){
                    if(validate()){
                        showProgress()
                        auth.createUserWithEmailAndPassword(teacher_gmail.text.toString(), teacher_password.text.toString())
                            .addOnCompleteListener(this@TeacherRegisterActivity) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(Constants.TAG, "createUserWithEmail:success")
                                    val user = auth.currentUser
                                    val details:UserDetails =
                                        UserDetails(teacher_name.text.toString(), null,teacher_gmail.text.toString(),
                                            teacher_address.text.toString(),teacher_mobile.text.toString(),Constants.TEACHER)
                                    db.collection(Constants.USER).document(user!!.uid).set(details)
                                    hideProgress()
                                    startActivity(Intent(this@TeacherRegisterActivity,
                                        LoginActivity::class.java))
                                    finish()
                                }
                                else {
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
        if(teacher_name.text.toString().length<6){
            Toast.makeText(this@TeacherRegisterActivity,"Enter Name Properly",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!EMAIL_ADDRESS.matcher(teacher_gmail.text.toString()).matches()){
            Toast.makeText(this@TeacherRegisterActivity,"Enter gmail Properly",Toast.LENGTH_SHORT).show()
            return false
        }
        if(teacher_address.text.toString().isEmpty()){
            Toast.makeText(this@TeacherRegisterActivity,"Enter Address Properly",Toast.LENGTH_SHORT).show()
            return false
        }
        if(teacher_mobile.text.toString().length!=10){
            Toast.makeText(this@TeacherRegisterActivity,"Enter Mobile Number Properly",Toast.LENGTH_SHORT).show()
            return false
        }

        if(teacher_password.text.toString().length!=8){
            Toast.makeText(this@TeacherRegisterActivity,"password length must be 8",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
