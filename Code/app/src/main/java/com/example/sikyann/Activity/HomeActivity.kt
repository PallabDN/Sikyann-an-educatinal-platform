package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class HomeActivity : RootActivity() {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser!!.uid


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(home_toolbar)
        home_toolbar.elevation = 20f

        showProgress()

        if(!isNetworkActive(this@HomeActivity)){
            showSnackbar(getString(R.string.network),home_view)
            home_view.visibility = View.GONE
        }

        update_ui()
        hideProgress()
        tittle.setOnClickListener {
            startActivity(Intent(this@HomeActivity,UserProfileActivity::class.java))
        }

        teacher_questionpaper.setOnClickListener {
            QuestionPapersActivity()

        }

        teacher_classroom.setOnClickListener {
            ClassRoomsActivity()
        }

        create_question_paper.setOnClickListener {
            onclick_questionpaper_activity()
        }
        create_classroom.setOnClickListener {
            onclick_classroom_activity()
        }



    }

    private fun update_ui(){

        val docRef = db.collection(Constants.USER).document(user)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(Constants.TAG, "Successfully fetched")
                    val data = document.toObject(UserDetails::class.java)!!
                    val occupation = data.occupation!!
                    tittle.text = data.name!!.toLowerCase(Locale.ROOT)
                    subtittle.text=data.occupation
                    gmail.text = data.gmail
                    if(occupation.contains(Constants.STUDENT)){
                        create_room_text.text = Constants.JOIN_ROOM
                        create_paper_text.text = Constants.ATTEMPT_PAPER
                    }
                } else {
                    Log.d(Constants.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
            }

    }

    fun onclick_classroom_activity(){
        val doc = db.collection(Constants.USER).document(user)
        doc.get()
            .addOnSuccessListener {
            val data = it.toObject(UserDetails::class.java)!!
            val x = data.occupation!!
            if(x.contains(Constants.STUDENT)){
                startActivity(Intent(this@HomeActivity,JoinClassRoomActivity::class.java))
            }else{
                startActivity(Intent(this@HomeActivity,CreateClassroomActivity::class.java))
            }
        }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
            }
    }
    fun onclick_questionpaper_activity(){
        val doc = db.collection(Constants.USER).document(user)
        doc.get()
            .addOnSuccessListener {
                val data = it.toObject(UserDetails::class.java)!!
                val x = data.occupation!!
                if(x.contains(Constants.STUDENT)){
                    startActivity(Intent(this@HomeActivity,AttemptQuestionPaperActivity::class.java))
                }else{
                    startActivity(Intent(this@HomeActivity,CreateQuestionPaperActivity::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
            }
    }


    fun ClassRoomsActivity(){
        val doc = db.collection(Constants.USER).document(user)
        doc.get()
            .addOnSuccessListener {
                val data = it.toObject(UserDetails::class.java)!!
                val x = data.occupation!!
                if(x.contains(Constants.STUDENT)){
                    startActivity(Intent(this@HomeActivity,ClassRoomsActivity::class.java))
                }else{
                    startActivity(Intent(this@HomeActivity,TeacherClassRoomsActivity::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
            }
    }


    fun QuestionPapersActivity(){
        val doc = db.collection(Constants.USER).document(user)
        doc.get()
            .addOnSuccessListener {
                val data = it.toObject(UserDetails::class.java)!!
                val x = data.occupation!!
                if(x.contains(Constants.STUDENT)){
                    startActivity(Intent(this@HomeActivity,QuestionPaperActivity::class.java))
                }else{
                    startActivity(Intent(this@HomeActivity,TeacherQuestPapersActivity::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(Constants.TAG, "get failed with ", exception)
            }
    }


}
