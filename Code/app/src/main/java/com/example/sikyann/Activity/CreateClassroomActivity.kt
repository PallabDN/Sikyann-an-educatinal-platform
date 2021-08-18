package com.example.sikyann.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_classroom.*
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*

class CreateClassroomActivity : RootActivity() {
    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_classroom)


        finish_create_room.setOnClickListener {
            if(!isNetworkActive(this@CreateClassroomActivity)){
                showSnackbar(getString(R.string.network),create_classroom_view)
                create_classroom_view.visibility = View.GONE
            }
            else{
                if(valid()){
                    showProgress()
                    val id = uniqueID()

                    val date = getDate()

                    val classRoomDetails = ClassRoomModel(classroom_title.text.toString(),
                        classroom_subtitle.text.toString(),classroom_description.text.toString(),currentUser()+"+"+id,date)

                    db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.CLASS_ROOM)
                        .document(id).set(classRoomDetails).addOnSuccessListener {
                            Log.d(Constants.TAG,"ClassRoom Successfully Created")
                            Toast.makeText(this@CreateClassroomActivity,"Room created successfully",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Log.d(Constants.TAG,"Failed to crate classroom")
                        }
                    hideProgress()

                    finish()
                }
            }


        }


    }





    fun valid() :Boolean{
        if(classroom_title.text.toString().isEmpty()){
            Toast.makeText(this@CreateClassroomActivity,"Enter the Title of Classroom",Toast.LENGTH_SHORT).show()
            return false
        }
        if(classroom_description.text.toString().isEmpty()){
            Toast.makeText(this@CreateClassroomActivity,"Enter the Description of Classroom",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}
