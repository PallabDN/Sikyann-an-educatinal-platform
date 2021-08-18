package com.example.sikyann.Activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Adapter.Adapter
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.Module.UniqueID
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_question_paper.*

class QuestionPaperActivity : RootActivity() {

    var questionList = mutableListOf<UniqueID>()
    var questionModelList = mutableListOf<QuestionPaperModel>()
    var questionAttemptDateList = mutableListOf<String>()
    var questionDocIdList = mutableListOf<String>()

    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_paper)

        setSupportActionBar(questionPaperToolbar)
        questionPaperToolbar.elevation = 20f

        if(!isNetworkActive(this@QuestionPaperActivity)){
            showSnackbar(getString(R.string.network),question_paper_view)
            question_paper_view.visibility = View.GONE
        }
        else{

            db.collection(Constants.USER).document(currentUser())
                .collection(Constants.QUESTION_PAPER)
                .addSnapshotListener { value, error ->
                    if(error!=null){
                        return@addSnapshotListener
                    }
                    questionList = value!!.toObjects(UniqueID::class.java)
                    if(questionList.isEmpty()){
                        questionPaper_recycler_view.visibility = View.GONE
                        student_questionPapers.visibility = View.VISIBLE
                    }else{
                        for(i in value){
                            questionDocIdList.add(i.id)
                        }
                        for(i in questionList.indices){
                            val pattern = Regex("\\+")
                            val list = pattern.split(questionList[i].id.toString()) as MutableList<String>
                            db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
                                .get().addOnSuccessListener {
                                    questionModelList.add(it.toObject(QuestionPaperModel::class.java)!!)
                                    questionAttemptDateList.add(questionList[i].date.toString())
                                    if(i==questionList.size-1){
                                        val adapter =
                                            Adapter(questionModelList,questionAttemptDateList,questionDocIdList,this@QuestionPaperActivity,
                                                SpecificQuestionPaperActivity::class.java,currentUser())
                                        questionPaper_recycler_view.adapter = adapter
                                        questionPaper_recycler_view.setHasFixedSize(true)
                                        questionPaper_recycler_view.layoutManager =
                                            LinearLayoutManager(this@QuestionPaperActivity,LinearLayoutManager.VERTICAL,false)
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
