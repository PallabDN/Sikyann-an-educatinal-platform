package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser!!.uid

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        if(!isNetworkActive(this@UserProfileActivity)){
            showSnackbar(getString(R.string.network),user_profile_view)
            user_profile_view.visibility = View.GONE
        }else{
            showProgress()
            db.collection(Constants.USER).document(user).get().addOnSuccessListener {document->
                if(document!=null){
                    val data = document.toObject(UserDetails::class.java)!!
                    userName.text = data.name.toString()
                    userMobile.text = data.mobile.toString()
                    userGamil.text = data.gmail.toString()
                    userAddress.text = data.address.toString()
                    if(data.father!=null){
                        userFather.text = data.father.toString()
                    }else{
                        userFather.visibility = View.GONE
                    }

                }else{
                    Log.d(Constants.TAG, "No such document")
                }
                hideProgress()
            }.addOnFailureListener { exception ->
                    Log.d(Constants.TAG, "get failed with ", exception)
                   hideProgress()
                }


            userLogOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@UserProfileActivity,LoginActivity::class.java))
                finish()
            }

            userEditDetails.setOnClickListener {
                startActivity(Intent(this@UserProfileActivity,UserEditDetailsActivity::class.java))
                finish()
            }


        }

    }
}
