package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.Module.StudentClassRoomDataModel
import com.example.sikyann.Module.UniqueID
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_attempt_question_paper.*
import kotlinx.android.synthetic.main.activity_join_class_room.*

class JoinClassRoomActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_class_room)

        if(!isNetworkActive(this@JoinClassRoomActivity)){
            showSnackbar(getString(R.string.network),joinClassRoomView)
            joinClassRoomView.visibility = View.GONE
        }


        enter_classroom.setOnClickListener {
            showProgress()
            val id=classroom_code.text.toString()
            val pattern = Regex("\\+")
            var list = mutableListOf<String>()
            list = pattern.split(id) as MutableList<String>

            val data = StudentClassRoomDataModel(getDate(),currentUser(),id)

            if(list.size!=2||list[0].isEmpty()||list[1].isEmpty()){
                hideProgress()
                Toast.makeText(this@JoinClassRoomActivity,"Enter Correct code", Toast.LENGTH_SHORT).show()
            }else{
                db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1])
                    .get().addOnSuccessListener { it ->
                        if(it.toObject(ClassRoomModel::class.java)!=null){
                            db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1])
                                .collection(Constants.STUDENT).document(currentUser()).set(data).addOnSuccessListener {
                                    Log.d(Constants.TAG,"Successfully Joined")
                                }.addOnFailureListener {x->
                                    hideProgress()
                                    Log.d(Constants.TAG,"Error $x")
                                }

                            db.collection(Constants.USER).document(currentUser()).collection(Constants.CLASS_ROOM).document(id).set(data)
                                .addOnSuccessListener {
                                    val intent = Intent(this@JoinClassRoomActivity,SpecificClassRoomActivity::class.java)
                                    intent.putExtra(Constants.CLASSROOM_ID,id)
                                    startActivity(intent)
                                    hideProgress()
                                    finish()
                                }
                                .addOnFailureListener {x->
                                    hideProgress()
                                    Log.d(Constants.TAG,"Error $x")
                                }
                        }else{
                            hideProgress()
                            Toast.makeText(this@JoinClassRoomActivity,"No such document is found",Toast.LENGTH_SHORT).show()

                        }
                    }.addOnFailureListener {
                        Log.d(Constants.TAG,it.toString())
                        Toast.makeText(this@JoinClassRoomActivity,"No such document is found",Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
            }


        }
    }
}
