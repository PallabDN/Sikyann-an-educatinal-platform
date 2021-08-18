package com.example.sikyann.Activity

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionModel
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.activity_specific_question_paper.*
import kotlinx.android.synthetic.main.activity_teacher_quest_papers.*

class SpecificQuestionPaperActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_question_paper)
        if(!isNetworkActive(this@SpecificQuestionPaperActivity)){
            showSnackbar(getString(R.string.network),specific_question_view)
            specific_question_view.visibility = View.GONE
        }else{
            showProgress()
            val id = intent.getStringExtra(Constants.QUESTION_ID)!!
            val docId = intent.getStringExtra(Constants.DOC_ID)!!

            val pattern = Regex("\\+")
            val list = pattern.split(id) as MutableList


            db.collection(Constants.USER).document(currentUser()).get()
                .addOnSuccessListener {
                    val user = it.toObject(UserDetails::class.java)!!
                    val occupation = user.occupation!!


                    //questions
                    db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1]).collection(Constants.QUESTIONS)
                        .addSnapshotListener { value, error ->
                            if(error!=null){
                                return@addSnapshotListener
                            }

                            val questions = value!!.toObjects(QuestionModel::class.java)
                            val questionsLayout = findViewById<LinearLayout>(R.id.specific_questions)
                            val layoutInflate = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            for(i in questions.indices){


                                val tempQuestion = layoutInflate.inflate(R.layout.question_,null)
                                val questionNumber = tempQuestion.findViewById<TextView>(R.id.question_layout_number)
                                val questionStatement = tempQuestion.findViewById<TextView>(R.id.question_layout_question)
                                val questionMark = tempQuestion.findViewById<TextView>(R.id.question_layout_mark)
                                val optionView = tempQuestion.findViewById<LinearLayout>(R.id.question_layout_option)
                                val questionImage = tempQuestion.findViewById<ImageView>(R.id.question_layout_image)
                                val correctAnswer = tempQuestion.findViewById<TextView>(R.id.correct_answer_question)

                                questionNumber.text = "${(i+1).toString()}. "
                                questionStatement.text = questions[i].question
                                questionMark.text = questions[i].mark
                                correctAnswer.visibility = View.VISIBLE
                                correctAnswer.text = "Answer: ${questions[i].answer}"



                                //update ui






                                if(occupation == Constants.TEACHER){
                                    performance.text = "Students Performance"
                                    performance.setOnClickListener {
                                        val intent = Intent(this@SpecificQuestionPaperActivity,StudentPerformanceActivity::class.java)
                                        intent.putExtra(Constants.QUESTION_ID,id)
                                        startActivity(intent)
                                    }
                                }else{
                                    correctAnswer.visibility = View.GONE
                                    performance.setOnClickListener {
                                        val intent = Intent(this@SpecificQuestionPaperActivity,PerformanceActivity::class.java)
                                        intent.putExtra(Constants.QUESTION_ID,id)

                                        intent.putExtra(Constants.DOC_ID,docId)
                                        startActivity(intent)
                                    }
                                }





                                val optionList = questions[i].option as MutableList
                                if(optionList.isNotEmpty()){
                                    for(j in optionList.indices){
                                        val tempOption = layoutInflate.inflate(R.layout.option_, null)
                                        val optionNumber = tempOption.findViewById<TextView>(R.id.option_no)
                                        val optionText = tempOption.findViewById<TextView>(R.id.option_text)

                                        optionNumber.text = (j+1).toString()
                                        optionText.text = optionList[j]

                                        optionView.addView(tempOption)
                                    }

                                }
                                if(questions[i].image!!.isNotEmpty()){
                                    Log.d(Constants.TAG,"image is loaded")
                                    questionImage.visibility = View.VISIBLE
                                    Glide
                                        .with(this@SpecificQuestionPaperActivity)
                                        .load(questions[i].image)
                                        .centerCrop()
                                        .into(questionImage)
                                }
                                questionsLayout.addView(tempQuestion)
                            }

                        }



                }


            db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
                .get().addOnSuccessListener {
                    val data = it.toObject(QuestionPaperModel::class.java)!!
                    specificQuestionPaperTitle.text = data.title
                    if(data.subtitle!!.isEmpty()){
                        specificQuestionPaperSubtitle.visibility = View.GONE
                    }else{
                        specificQuestionPaperSubtitle.text = data.subtitle
                    }

                    specificQuestionPaperTotalMarks.text = "Total Marks: ${data.totalMark}"
                    specific_code.text = data.uniqueId

                    specific_copy.setOnClickListener {
                        CopyToClipboard(data.uniqueId.toString())
                        Toast.makeText(this@SpecificQuestionPaperActivity,"Code copied", Toast.LENGTH_SHORT).show()
                    }

                }


            hideProgress()
        }


    }
}
