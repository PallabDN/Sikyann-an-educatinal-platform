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
import com.example.sikyann.Module.StudentResltModel
import com.example.sikyann.Module.UniqueID
import com.example.sikyann.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_attempt_question_paper.*
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.question_.view.*
import java.util.*

class QuestionActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    var resultList = mutableListOf<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)



        val id = intent.getStringExtra(Constants.QUESTION_ID)!!

        val pattern = Regex("\\+")
        val list = pattern.split(id) as MutableList


        if(!isNetworkActive(this@QuestionActivity)){
            showSnackbar(getString(R.string.network),question_view)
            question_view.visibility = View.GONE
        }


        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
            .get().addOnSuccessListener {
                Log.d(Constants.TAG,id)
                if(it!=null){
                    val doc = it.toObject(QuestionPaperModel::class.java)!!
                    questionPaperTitle.text = doc.title
                    if(doc.subtitle!!.isEmpty()){
                        questionPaperSubtitle.visibility = View.GONE
                    }else{
                        questionPaperSubtitle.text = doc.subtitle
                    }
                    questionPaperTotalMarks.text = "Marks: ${doc.totalMark}"
                }
                else{
                    Log.d(Constants.TAG,"No such document found")
                }
            }

        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
            .collection(Constants.QUESTIONS).addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }

                var i = 0
                var mark = 0
                val questions = value!!.toObjects(QuestionModel::class.java)

                val questionsLayout = findViewById<LinearLayout>(R.id.questions)
                val layoutInflate = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val tempQuestion = layoutInflate.inflate(R.layout.question_,null)
                val questionNumber = tempQuestion.findViewById<TextView>(R.id.question_layout_number)
                val questionStatement = tempQuestion.findViewById<TextView>(R.id.question_layout_question)
                val questionMark = tempQuestion.findViewById<TextView>(R.id.question_layout_mark)
                val optionView = tempQuestion.findViewById<LinearLayout>(R.id.question_layout_option)
                val questionImage = tempQuestion.findViewById<ImageView>(R.id.question_layout_image)

                questionNumber.text = (i+1).toString()
                questionStatement.text = questions[i].question
                questionMark.text = questions[i].mark
                val optionList = questions[i].option as MutableList
                answer.inputType = InputType.TYPE_CLASS_TEXT
                if(optionList.isNotEmpty()){
                    for(j in optionList.indices){
                        val tempOption = layoutInflate.inflate(R.layout.option_, null)
                        val optionNumber = tempOption.findViewById<TextView>(R.id.option_no)
                        val optionText = tempOption.findViewById<TextView>(R.id.option_text)

                        optionNumber.text = (j+1).toString()
                        optionText.text = optionList[j]

                        optionView.addView(tempOption)
                    }
                    answer.inputType = InputType.TYPE_CLASS_NUMBER
                }
                if(questions[i].image!!.isNotEmpty()){
                    Log.d(Constants.TAG,"image is loaded")
                    questionImage.visibility = View.VISIBLE
                    Glide
                        .with(this@QuestionActivity)
                        .load(questions[i].image)
                        .centerCrop()
                        .into(questionImage)
                }
                questionsLayout.addView(tempQuestion)






                //Button



                next.setOnClickListener {
                    if(answer.text.toString().isEmpty()){
                        Toast.makeText(this@QuestionActivity,"Enter questions answer",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val image = questionsLayout.getChildAt(i)
                        image.answer_question.visibility = View.VISIBLE
                        image.answer_question.text = "Your Answer:  ${answer.text.toString()}"
                        resultList.add(answer.text.toString())
                        if(questions[i].answer!!.toLowerCase(Locale.ROOT) == answer.text.toString().toLowerCase(Locale.ROOT)) {
                            Log.d(Constants.TAG,"${questions[i].answer}     |    ${answer.text.toString()}")
                            mark += questions[i].mark!!.toInt()
                        }
                        else{

                            Log.d(Constants.TAG,"${questions[i].answer}     |    ${answer.text.toString()}")
                        }




                        if(i<questions.size-1){

                            val questionsLayout = findViewById<LinearLayout>(R.id.questions)
                            val layoutInflate = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val tempQuestion = layoutInflate.inflate(R.layout.question_,null)
                            val questionNumber = tempQuestion.findViewById<TextView>(R.id.question_layout_number)
                            val questionStatement = tempQuestion.findViewById<TextView>(R.id.question_layout_question)
                            val questionMark = tempQuestion.findViewById<TextView>(R.id.question_layout_mark)
                            val optionView = tempQuestion.findViewById<LinearLayout>(R.id.question_layout_option)
                            val questionImage = tempQuestion.findViewById<ImageView>(R.id.question_layout_image)
                            val answer_question = tempQuestion.findViewById<TextView>(R.id.answer_question)
                            val correct_answer = tempQuestion.findViewById<TextView>(R.id.correct_answer_question)




                            answer.text!!.clear()

                            i+=1

                            questionNumber.text = (i+1).toString()
                            questionStatement.text = questions[i].question
                            questionMark.text = questions[i].mark
                            val optionList = questions[i].option as MutableList

                            answer.inputType = InputType.TYPE_CLASS_TEXT
                            if(optionList.isNotEmpty()){
                                for(j in optionList.indices){
                                    val tempOption = layoutInflate.inflate(R.layout.option_, null)
                                    val optionNumber = tempOption.findViewById<TextView>(R.id.option_no)
                                    val optionText = tempOption.findViewById<TextView>(R.id.option_text)

                                    optionNumber.text = (j+1).toString()
                                    optionText.text = optionList[j]

                                    optionView.addView(tempOption)
                                }
                                answer.inputType = InputType.TYPE_CLASS_NUMBER
                            }
                            if(questions[i].image!!.isNotEmpty()){
                                Log.d(Constants.TAG,"image is loaded")
                                questionImage.visibility = View.VISIBLE
                                Glide
                                    .with(this@QuestionActivity)
                                    .load(questions[i].image)
                                    .centerCrop()
                                    .into(questionImage)
                            }


                            questionsLayout.addView(tempQuestion)

                        }else{

                            if(next.text != "Submit"){
                                next.text = "Submit"
                                answer_layout.visibility = View.GONE
                            }else{
                                val result = StudentResltModel(currentUser(),resultList,getDate(),getTime())
                                db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
                                    .collection(Constants.STUDENT).add(result).addOnSuccessListener {
                                        Log.d(Constants.TAG,"successfully added result")
                                    }
                                db.collection(Constants.USER).document(currentUser()).collection(Constants.QUESTION_PAPER).
                                add(UniqueID(id,resultList,getDate(),getTime()))
                                    .addOnSuccessListener {
                                        val id_doc = it.id
                                        val intent = Intent(this@QuestionActivity,PerformanceActivity::class.java)
                                        intent.putExtra(Constants.DOC_ID,id_doc)
                                        intent.putExtra(Constants.QUESTION_ID,id)
                                        startActivity(intent)
                                        finish()
                                    }
                            }
                        }
                    }


                }

            }
    }

    override fun onBackPressed() {
        Toast.makeText(this@QuestionActivity,"Please complete the Question Paper",Toast.LENGTH_SHORT).show()
    }
}
