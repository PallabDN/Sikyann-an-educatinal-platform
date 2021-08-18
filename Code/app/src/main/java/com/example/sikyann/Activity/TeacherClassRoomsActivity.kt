package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.TeacherClassRoomAdapter
import com.example.sikyann.Adapter.TeacherQuestionPaperAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_class_rooms.*
import kotlinx.android.synthetic.main.activity_teacher_class_rooms.*

class TeacherClassRoomsActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    var list = mutableListOf<ClassRoomModel>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_class_rooms)

        setSupportActionBar(teacher_classroom)
        teacher_classroom.elevation = 20f

        if(!isNetworkActive(this@TeacherClassRoomsActivity)){
            showSnackbar(getString(R.string.network),teacher_classRoom_view)
            teacher_classRoom_view.visibility = View.GONE
        }

        db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.CLASS_ROOM)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                list = value!!.toObjects(ClassRoomModel::class.java)
                if(list.isEmpty()){
                    teacher_NoClassRoom.visibility = View.VISIBLE
                    teacher_classRoom_recycler_view.visibility = View.GONE
                }else{
                    val adapter = TeacherClassRoomAdapter(list,this@TeacherClassRoomsActivity,SpecificClassRoomActivity::class.java)
                    teacher_classRoom_recycler_view.adapter = adapter
                    teacher_classRoom_recycler_view.setHasFixedSize(true)
                    teacher_classRoom_recycler_view.layoutManager =
                        LinearLayoutManager(this@TeacherClassRoomsActivity,LinearLayoutManager.VERTICAL,false)
                }
            }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.classroom, menu)

        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.create_class){
            startActivity(Intent(this@TeacherClassRoomsActivity,CreateClassroomActivity::class.java))
            return true
        }
        return false
    }


    class viewHolder(view:View): RecyclerView.ViewHolder(view){}

}
