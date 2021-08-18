package com.example.sikyann.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.sikyann.Constants
import com.example.sikyann.Constants.PICK_IMAGE_REQUEST
import com.example.sikyann.Module.QuestionModel
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_create_question_paper.*
import kotlinx.android.synthetic.main.activity_home.*
import java.io.IOException
import java.util.*


class CreateQuestionPaperActivity : RootActivity() {
    lateinit var question:String
    lateinit var marks:String
    lateinit var answer:String
    var total_mark = 0
    var option_list = mutableListOf<String>()
    var image_list = mutableListOf<String>()
    var filePath: Uri? = null

    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = storage.reference
    var downloadUri:String = ""

    var clickOnce:Boolean = true

    val db = FirebaseFirestore.getInstance()


    private val uniqueID = uniqueID()


    lateinit var questionModel:QuestionModel

    var index = 1


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_question_paper)


        total_mark_view.text = total_mark.toString()



        add_options.setOnClickListener {
            val option = questions_option.text.toString()
            if(option.isEmpty()){
                Toast.makeText(
                    this@CreateQuestionPaperActivity,
                    "Provide question's option",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                option_list.add(option)
                questions_answer.inputType = InputType.TYPE_CLASS_NUMBER
            }
            CreateAndAppendOptionListLayout()
            questions_option.text!!.clear()

        }







        questionImage.setOnClickListener {
            if(filePath!=null){
// Code for showing progressDialog while uploading
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

// Defining the child of storageReference
                val imageID = index.toString()
                val ref: StorageReference = storageReference.child("question_images/"+currentUser()+"/"+imageID)
// adding listeners on upload
// or failure of image
                val uploadTask = ref.putFile(filePath!!)
                    .addOnSuccessListener { // Image uploaded successfully
                        // Dismiss dialog
                        clickOnce = true
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@CreateQuestionPaperActivity,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        image_list.add(imageID)
                    }
                    .addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@CreateQuestionPaperActivity,
                                "Failed " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    .addOnProgressListener(
                        object : OnProgressListener<UploadTask.TaskSnapshot?> {
                            // Progress Listener for loading
                            // percentage on the dialog box
                            override fun onProgress(
                                taskSnapshot: UploadTask.TaskSnapshot
                            ) {
                                val progress = ((100.0
                                        * taskSnapshot.bytesTransferred
                                        / taskSnapshot.totalByteCount))
                                progressDialog.setMessage(
                                    ("Uploaded $progress%")
                                )
                            }
                        })
                downloadUri = ""
                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        downloadUri = task.result!!.toString()
                    }
                }
            }
            else{
                // Defining Implicit Intent to mobile gallery
                // Defining Implicit Intent to mobile gallery
                val intent = Intent()

                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image from here..."),
                    PICK_IMAGE_REQUEST
                )
            }
        }






        question_next.setOnClickListener {

            if(!isNetworkActive(this@CreateQuestionPaperActivity)){
                showSnackbar(getString(R.string.network),create_question_paper_view)
            }

            else{
                if(validate()){
                    showProgress()
                    question = question_question.text.toString()
                    marks = question_marks.text.toString()
                    answer = questions_answer.text.toString()
                    Log.d(Constants.TAG,"Add to List")
                    questionModel = QuestionModel(question, marks, option_list, downloadUri, answer)

                    db.collection(Constants.TEACHER).document(currentUser())
                        .collection(Constants.QUESTION_PAPER).document(uniqueID)
                        .collection(Constants.QUESTIONS).document(index.toString())
                        .set(questionModel)
                        .addOnSuccessListener {

                            CreateAndAppendQuestionListLayout()

                            index+=1
                            line_background.visibility = View.VISIBLE
                            total_mark += marks.toInt()
                            total_mark_view.text = "Total marks: ${total_mark.toString()}"
                            question_question.text!!.clear()
                            questions_answer.inputType = InputType.TYPE_CLASS_TEXT
                            question_marks.text!!.clear()
                            questions_answer.text!!.clear()
                            downloadUri = ""
                            imageView.visibility = View.GONE
                            option_question.removeAllViews()
                            option_list.clear()
                            hideProgress()
                            Log.d(Constants.TAG,"Add question paper successful")
                        }
                        .addOnFailureListener {
                            hideProgress()
                            Log.d(Constants.TAG,"failed $it")
                        }

                }
            }
        }






        question_finish.setOnClickListener {
            if(!isNetworkActive(this@CreateQuestionPaperActivity)){
                showSnackbar(getString(R.string.network),create_question_paper_view)
            }

            else{
                val title = question_title.text.toString()
                val subtitle = question_subtitle.text.toString()
                if(title.isEmpty()){
                    Toast.makeText(this@CreateQuestionPaperActivity, "Enter Question Paper Title", Toast.LENGTH_SHORT).show()
                }
                else{
                    showProgress()

                    val date = getDate()
                    val model = QuestionPaperModel(title,subtitle,currentUser()+"+"+uniqueID,image_list,total_mark.toString(),date)

                    db.collection(Constants.TEACHER).document(currentUser())
                        .collection(Constants.QUESTION_PAPER).document(uniqueID)
                        .set(model)
                        .addOnSuccessListener {
                            hideProgress()
                            Log.d(Constants.TAG,"Add question paper Model successful")
                            finish()
                        }
                        .addOnFailureListener {
                            hideProgress()
                            Log.d(Constants.TAG,"failed $it")
                        }
                }
            }


        }


    }













    fun validate():Boolean{
        question = question_question.text.toString()
        marks = question_marks.text.toString()
        answer = questions_answer.text.toString()
        if(question.isEmpty()){
            Toast.makeText(this@CreateQuestionPaperActivity, "Provide question", Toast.LENGTH_SHORT).show()
            return false
        }
        if(marks.isEmpty()){
            Toast.makeText(
                this@CreateQuestionPaperActivity,
                "Provide question's mark",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if(answer.isEmpty()){
            Toast.makeText(
                this@CreateQuestionPaperActivity,
                "Provide question's answer",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!clickOnce){
            Toast.makeText(this@CreateQuestionPaperActivity, "Please Upload the selected image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    fun CreateAndAppendOptionListLayout() {
        val mainLayout = findViewById<View>(R.id.option_question) as LinearLayout
        val li = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mainLayout.removeAllViews()
        for (i in option_list.indices) {
            val tempView: View = li.inflate(R.layout.option_, null)
            val text_option = tempView.findViewById<TextView>(R.id.option_text)
            val option_no = tempView.findViewById<TextView>(R.id.option_no)
            text_option.text = option_list[i]
            option_no.text = (i+1).toString()
            mainLayout.addView(tempView)
        }
    }


    fun CreateAndAppendQuestionListLayout(){
        val finalQuestion = findViewById<LinearLayout>(R.id.final_question)
        val layoutInfate = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val tempQuestion = layoutInfate.inflate(R.layout.question_, null)      //125
        val questionNumber = tempQuestion.findViewById<TextView>(R.id.question_layout_number)
        val questionStatement = tempQuestion.findViewById<TextView>(R.id.question_layout_question)
        val questionMark = tempQuestion.findViewById<TextView>(R.id.question_layout_mark)
        val optionView = tempQuestion.findViewById<LinearLayout>(R.id.question_layout_option)
        val questionImage = tempQuestion.findViewById<ImageView>(R.id.question_layout_image)


        questionNumber.text = index.toString()
        questionStatement.text = questionModel.question
        questionMark.text = questionModel.mark
        questionImage.visibility = View.GONE
        if(questionModel.image!!.isNotEmpty()){
            Log.d(Constants.TAG,"image is loaded")
            questionImage.visibility = View.VISIBLE
            Glide
                .with(this@CreateQuestionPaperActivity)
                .load(questionModel.image)
                .centerCrop()
                .into(questionImage)
        }
        val optionList = questionModel.option as MutableList
        if(optionList.isNotEmpty()){
            for(j in optionList.indices){
                val tempOption = layoutInfate.inflate(R.layout.option_, null)
                val optionNumber = tempOption.findViewById<TextView>(R.id.option_no)
                val optionText = tempOption.findViewById<TextView>(R.id.option_text)

                optionNumber.text = (j+1).toString()
                optionText.text = optionList[j]

                optionView.addView(tempOption)
            }
        }
        finalQuestion.addView(tempQuestion)
    }






    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            filePath = data.data!!
            clickOnce = false
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                imageView.visibility = View.VISIBLE
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {

        if(!isNetworkActive(this@CreateQuestionPaperActivity)){
            showSnackbar(getString(R.string.network),create_question_paper_view)
        }

        else{
            showProgress()

            db.collection(Constants.TEACHER).document(currentUser())
                .collection(Constants.QUESTION_PAPER).document(uniqueID)
                .delete()
                .addOnSuccessListener {
                    Log.d(Constants.TAG,"Question paper Delete Successfully")
                    if(image_list.isEmpty()){
                        hideProgress()
                        finish()
                    }else{
                        for(i in image_list){
                            val ref: StorageReference = storageReference.child("question_images/"+currentUser()+"/"+uniqueID+"/"+i)
                            ref.delete().addOnSuccessListener {
                                Log.d(Constants.TAG,"Question Images Delete Successfully")
                                hideProgress()
                                Toast.makeText(this@CreateQuestionPaperActivity,"Question Paper dismissed",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                                .addOnFailureListener {
                                    hideProgress()
                                    Log.d(Constants.TAG, "Question Images Delete Failed$it")
                                }
                        }
                    }
                }.addOnFailureListener {
                    hideProgress()
                    Log.d(Constants.TAG,"Question Delete Failed$it")
                }
        }

    }

}




