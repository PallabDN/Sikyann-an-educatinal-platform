package com.example.sikyann.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.Adapter
import com.example.sikyann.Adapter.ClassRoomAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.Module.StudentClassRoomDataModel
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_class_rooms.*
import kotlinx.android.synthetic.main.activity_home.*

class ClassRoomsActivity : RootActivity(){

    val db = FirebaseFirestore.getInstance()
    var classRoomIDList = mutableListOf<StudentClassRoomDataModel>()
    var classRoomList = mutableListOf<ClassRoomModel>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_rooms)
        setSupportActionBar(classroom)
        classroom.elevation = 20f
        if(!isNetworkActive(this@ClassRoomsActivity)){
            showSnackbar(getString(R.string.network),classRoom_view)
            classRoom_view.visibility = View.GONE
        }
        db.collection(Constants.USER).document(currentUser()).collection(Constants.CLASS_ROOM)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                val pattern = Regex("\\+")
                classRoomIDList = value!!.toObjects(StudentClassRoomDataModel::class.java)
                if(classRoomIDList.isEmpty()){
                    classRoom_recycler_view.visibility = View.GONE
                    student_NoClassRoom.visibility = View.VISIBLE
                }else{
                    for(i in classRoomIDList.indices){
                        val data = classRoomIDList[i]
                        val list = pattern.split(data.classRoomPath.toString()) as MutableList
                        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.CLASS_ROOM).document(list[1])
                            .get().addOnSuccessListener {
                                if(it!=null){
                                    classRoomList.add(it.toObject(ClassRoomModel::class.java)!!)
                                    if(i == classRoomIDList.size-1){
                                        val adapter = ClassRoomAdapter(classRoomList,classRoomIDList,this@ClassRoomsActivity,SpecificClassRoomActivity::class.java)
                                        classRoom_recycler_view.adapter = adapter
                                        classRoom_recycler_view.setHasFixedSize(true)
                                        classRoom_recycler_view.layoutManager =
                                            LinearLayoutManager(this@ClassRoomsActivity,LinearLayoutManager.VERTICAL,false)
                                    }
                                }
                            }
                    }
                }
            }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

}
