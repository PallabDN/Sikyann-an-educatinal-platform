package com.example.sikyann.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.StudentPerformanceAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionModel
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.Module.StudentResltModel
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_specific_question_paper.*
import kotlinx.android.synthetic.main.activity_student_performance.*

class StudentPerformanceActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    val answerList = mutableListOf<String>()
    val markList = mutableListOf<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_performance)

        val id = intent.getStringExtra(Constants.QUESTION_ID)!!
        val pattern = Regex("\\+")
        val list = pattern.split(id) as MutableList


        if(!isNetworkActive(this@StudentPerformanceActivity)){
            showSnackbar(getString(R.string.network),student_performance_view)
            student_performance_view.visibility = View.GONE
        }else{
            db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.QUESTION_PAPER).document(list[1])
                .collection(Constants.QUESTIONS).addSnapshotListener { value, error ->
                    if(error!=null){
                        return@addSnapshotListener
                    }
                    val x = value!!.toObjects(QuestionModel::class.java) as MutableList
                    for(i in x){
                        answerList.add(i.answer.toString())
                        markList.add(i.mark.toString())
                    }
                    db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.QUESTION_PAPER).document(list[1])
                        .collection(Constants.STUDENT).addSnapshotListener { va, e ->
                            if(e!=null){
                                return@addSnapshotListener
                            }
                            val studentList = va!!.toObjects(StudentResltModel::class.java) as MutableList
                            if(studentList.isEmpty()){
                                noStudent.visibility = View.VISIBLE
                                student_performance.visibility = View.GONE
                            }else{
                                val adapter = StudentPerformanceAdapter(studentList,answerList,markList)
                                student_performance.adapter = adapter
                                student_performance.setHasFixedSize(true)
                                student_performance.layoutManager =
                                    LinearLayoutManager(this@StudentPerformanceActivity,LinearLayoutManager.VERTICAL,false)
                            }
                        }
                }

        }


    }


    class viewHolder(view:View): RecyclerView.ViewHolder(view){
    }

}


