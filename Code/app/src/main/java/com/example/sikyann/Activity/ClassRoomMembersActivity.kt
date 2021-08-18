package com.example.sikyann.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.ClassRoomMemberAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.StudentClassRoomDataModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_class_room_members.*
import kotlinx.android.synthetic.main.activity_class_rooms.*

class ClassRoomMembersActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    var data = mutableListOf<StudentClassRoomDataModel>()
    var ListStudent = mutableListOf<UserDetails>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_room_members)

        val id = intent.getStringExtra(Constants.CLASSROOM_ID).toString()
        val pattern = Regex("\\+")
        val list = pattern.split(id) as MutableList

        if(!isNetworkActive(this@ClassRoomMembersActivity)){
            showSnackbar(getString(R.string.network),classRoom_members_view)
            classRoom_members_view.visibility = View.GONE
        }

        db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.CLASS_ROOM).document(list[1]).collection(Constants.STUDENT)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                data = value!!.toObjects(StudentClassRoomDataModel::class.java)
                if(data.isEmpty()){
                    classRoomMembers.visibility = View.GONE
                    noStudentsText.visibility = View.VISIBLE
                }else{
                    for(i in data.indices){
                        db.collection(Constants.USER).document(data[i].studetID!!).get()
                            .addOnSuccessListener {
                                val x = it.toObject(UserDetails::class.java)!!
                                ListStudent.add(x)
                                if(i==data.size-1){
                                    val adapter = ClassRoomMemberAdapter(data,ListStudent,this@ClassRoomMembersActivity)
                                    classRoomMembers.adapter = adapter
                                    classRoomMembers.setHasFixedSize(true)
                                    classRoomMembers.layoutManager =
                                        LinearLayoutManager(this@ClassRoomMembersActivity,LinearLayoutManager.VERTICAL,false)
                                }
                            }
                    }
                }

            }


    }



    class viewHolder(view: View): RecyclerView.ViewHolder(view){

    }
}
