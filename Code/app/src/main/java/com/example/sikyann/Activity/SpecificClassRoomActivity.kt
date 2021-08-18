package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.ClassRoomContentAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomContentModel
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.UserDataWriter
import kotlinx.android.synthetic.main.activity_class_rooms.*
import kotlinx.android.synthetic.main.activity_create_classroom.*
import kotlinx.android.synthetic.main.activity_specific_class_room.*

class SpecificClassRoomActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_class_room)

        val id = intent.getStringExtra(Constants.CLASSROOM_ID).toString()
        val pattern = Regex("\\+")
        val list = pattern.split(id) as MutableList

        if(!isNetworkActive(this@SpecificClassRoomActivity)){
            showSnackbar(getString(R.string.network),specific_classRoom_view)
            specific_classRoom_view.visibility = View.GONE
        }

        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1])
            .get().addOnSuccessListener {
                Log.d(Constants.TAG,list[0]+"        "+list[1])
                val data = it.toObject(ClassRoomModel::class.java)!!
                specific_classRoomTitle.text = data.title
                if(data.subtitle!!.isEmpty()){
                    specific_classRoomSubtitle.visibility = View.GONE
                }else{
                    specific_classRoomSubtitle.text = data.subtitle
                }
                if(data.description!!.isEmpty()){
                    specific_classroom_description.visibility = View.GONE
                }else{
                    specific_classroom_description.text = data.description
                }
                specific_code_classRoom.text = data.uniqueId
                specific_copy_classRoom.setOnClickListener {
                    CopyToClipboard(data.uniqueId!!)
                    Toast.makeText(this@SpecificClassRoomActivity,"Text Copies!",Toast.LENGTH_SHORT).show()
                }

            }


        db.collection(Constants.USER).document(currentUser()).get().addOnSuccessListener {
            val user = it.toObject(UserDetails::class.java)!!
            if(user.occupation == Constants.TEACHER){
                classroomButton.visibility = View.VISIBLE
                studentExit.visibility = View.GONE
            }else{
                specific_copy_code_classRoom.visibility = View.GONE
            }
        }

        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1]).collection(Constants.CONTENTS)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                val listContent = value!!.toObjects(ClassRoomContentModel::class.java) as MutableList
                if(listContent.isEmpty()){
                    classroom_content.visibility = View.GONE
                    NoClassRoomContent.visibility = View.VISIBLE
                }else{
                    val adapter = ClassRoomContentAdapter(listContent,this@SpecificClassRoomActivity,ViewPDFActivity::class.java)
                    classroom_content.adapter = adapter
                    classroom_content.setHasFixedSize(true)
                    classroom_content.layoutManager = LinearLayoutManager(this@SpecificClassRoomActivity,LinearLayoutManager.VERTICAL,false)
                }
            }



        uploadClassroomContent.setOnClickListener {
            val intent = Intent(this@SpecificClassRoomActivity,UploadClassRoomContentActivity::class.java)
            intent.putExtra(Constants.CLASSROOM_ID,id)
            startActivity(intent)
        }



        classMembers.setOnClickListener {
            val intent = Intent(this@SpecificClassRoomActivity,ClassRoomMembersActivity::class.java)
            intent.putExtra(Constants.CLASSROOM_ID,id)
            startActivity(intent)
        }


        studentExit.setOnClickListener {
            showProgress()
            db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1]).collection(Constants.STUDENT)
                .document(currentUser()).delete().addOnSuccessListener {
                    db.collection(Constants.USER).document(currentUser()).collection(Constants.CLASS_ROOM).document(id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@SpecificClassRoomActivity,"Successfully Leave the Room",Toast.LENGTH_SHORT).show()
                            hideProgress()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@SpecificClassRoomActivity,"Something Wrong",Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this@SpecificClassRoomActivity,"Something Wrong",Toast.LENGTH_SHORT).show()
                }
            hideProgress()
        }

    }


    class viewHolder(view: View): RecyclerView.ViewHolder(view){

    }

}
