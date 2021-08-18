package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_edit_details.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserEditDetailsActivity : RootActivity() {


    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser!!.uid


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_details)
        if (!isNetworkActive(this@UserEditDetailsActivity)) {
            showSnackbar(getString(R.string.network), userEditDetailsView)
            userEditDetailsView.visibility = View.GONE
        } else {
            showProgress()
            db.collection(Constants.USER).document(user).get().addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.toObject(UserDetails::class.java)!!
                    userEditName.setText(data.name.toString(), TextView.BufferType.EDITABLE)
                    userEditGmail.setText(data.gmail.toString(), TextView.BufferType.EDITABLE)
                    userEditMobile.setText(data.mobile.toString(), TextView.BufferType.EDITABLE)
                    userEditAddress.setText(data.address.toString(), TextView.BufferType.EDITABLE)
                } else {
                    Log.d(Constants.TAG, "No such document")
                }
                hideProgress()
            }.addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
                hideProgress()
            }

            userEditSaveChanges.setOnClickListener {
                if (userEditMobile.text.toString().length != 10) {
                    Toast.makeText(
                        this@UserEditDetailsActivity,
                        "Enter Mobile Number Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (userEditAddress.text!!.isEmpty()) {
                    Toast.makeText(
                        this@UserEditDetailsActivity,
                        "Enter Address Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val x = db.collection(Constants.USER).document(user)
                    x.update("mobile", userEditMobile.text.toString())
                        .addOnSuccessListener {
                            x.update("address", userEditAddress.text.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@UserEditDetailsActivity,
                                        "Changes Saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@UserEditDetailsActivity,UserProfileActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@UserEditDetailsActivity,
                                        "Something Wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@UserEditDetailsActivity,
                                "Something Wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }
        }
    }
}
