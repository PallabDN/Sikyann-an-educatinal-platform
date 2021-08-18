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
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.Module.UniqueID
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_attempt_question_paper.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_join_class_room.*

class AttemptQuestionPaperActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_question_paper)
        if(!isNetworkActive(this@AttemptQuestionPaperActivity)){
            showSnackbar(getString(R.string.network),attempt_question_paper_view)
            attempt_question_paper_view.visibility = View.GONE
        }


       enter_question.setOnClickListener {
           showProgress()
           val id = question_paper_code.text.toString()
           val pattern = Regex("\\+")
           var list = mutableListOf<String>()
           list = pattern.split(id) as MutableList<String>
           if(list.size!=2||list[0].isEmpty()||list[1].isEmpty()){
               hideProgress()
               Toast.makeText(this@AttemptQuestionPaperActivity,"Enter Correct code",Toast.LENGTH_SHORT).show()
           }else{
               db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER)
                   .document(list[1]).get().addOnSuccessListener {
                       if(it.toObject(QuestionPaperModel::class.java)!=null){
                           Log.d(Constants.TAG,"Document is found  ${list[1]}")
                           val intent = Intent(this@AttemptQuestionPaperActivity,QuestionActivity::class.java)
                           intent.putExtra(Constants.QUESTION_ID,id)
                           hideProgress()
                           startActivity(intent)
                           finish()
                       }
                       else{
                           hideProgress()
                           Toast.makeText(this@AttemptQuestionPaperActivity,"No such document is found",Toast.LENGTH_SHORT).show()

                       }
                   }
                   .addOnFailureListener {
                       Log.d(Constants.TAG,it.toString())
                       Toast.makeText(this@AttemptQuestionPaperActivity,"No such document is found",Toast.LENGTH_SHORT).show()
                       hideProgress()
                   }
           }
       }


    }
}
