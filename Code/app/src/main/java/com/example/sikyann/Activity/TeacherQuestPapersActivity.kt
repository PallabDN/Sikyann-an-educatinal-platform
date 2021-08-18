package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.Adapter
import com.example.sikyann.Adapter.TeacherQuestionPaperAdapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_teacher_quest_papers.*


class TeacherQuestPapersActivity : RootActivity() {

    val db=  FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_quest_papers)
        setSupportActionBar(teacher_question_paper)
        teacher_question_paper.elevation = 20f

        if(!isNetworkActive(this@TeacherQuestPapersActivity)){
            showSnackbar(getString(R.string.network),teacher_questionPaper_view)
            teacher_questionPaper_view.visibility = View.GONE
        }else{
            db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.QUESTION_PAPER).addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                val list = value!!.toObjects(QuestionPaperModel::class.java) as MutableList
                if(list.isEmpty()){
                    teacher_questionPaper_recycler_view.visibility = View.GONE
                    teacher_noQuestionPaper.visibility = View.VISIBLE
                }else{
                    val adapter = TeacherQuestionPaperAdapter(list,this@TeacherQuestPapersActivity,SpecificQuestionPaperActivity::class.java)
                    teacher_questionPaper_recycler_view.adapter = adapter
                    teacher_questionPaper_recycler_view.setHasFixedSize(true)
                    teacher_questionPaper_recycler_view.layoutManager =
                        LinearLayoutManager(this@TeacherQuestPapersActivity,LinearLayoutManager.VERTICAL,false)
                }
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
            startActivity(Intent(this@TeacherQuestPapersActivity,CreateQuestionPaperActivity::class.java))
            return true
        }
        return false
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

}
