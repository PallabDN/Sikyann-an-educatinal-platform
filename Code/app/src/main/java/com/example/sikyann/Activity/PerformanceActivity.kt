package com.example.sikyann.Activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionModel
import com.example.sikyann.Module.UniqueID
import com.example.sikyann.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_performance.*
import java.util.*

class PerformanceActivity : RootActivity() {


    val db = FirebaseFirestore.getInstance()
    val barList = mutableListOf<BarEntry>()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        if(!isNetworkActive(this@PerformanceActivity)){
            showSnackbar(getString(R.string.network),performanceView)
            performanceView.visibility = View.GONE
        }

        showProgress()

        val chart = findViewById<BarChart>(R.id.resultBarChart)

        val id_doc = intent.getStringExtra(Constants.DOC_ID)!!
        val id = intent.getStringExtra(Constants.QUESTION_ID)!!


        val pattern = Regex("\\+")
        val list = pattern.split(id) as MutableList


        db.collection(Constants.TEACHER).document(list[0]).collection(Constants.QUESTION_PAPER).document(list[1])
            .collection(Constants.QUESTIONS).addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                val questions = value!!.toObjects(QuestionModel::class.java)



                db.collection(Constants.USER).document(currentUser()).collection(Constants.QUESTION_PAPER).document(id_doc)
                    .get().addOnSuccessListener {
                        val result = it.toObject(UniqueID::class.java)!!.result as MutableList

                        for(i in questions.indices){

                            if(questions[i].answer.toString().toLowerCase(Locale.ROOT)== result[i].toLowerCase(Locale.ROOT)) {
                                barList.add(BarEntry((i+1).toFloat(),questions[i].mark!!.toFloat()))
                                Log.d(Constants.TAG,id_doc + i.toString()+"  "+result[i])
                            }
                            else{
                                barList.add(BarEntry((i+1).toFloat(),0f))
                            }
                        }

                        val barDataSet = BarDataSet(barList,"Result")
                        val barData = BarData(barDataSet)

                        // adding color to our bar data set.
                        barDataSet.setColors(Color.BLUE)
                        // setting text color.
                        barDataSet.valueTextColor = Color.BLACK
                        // setting text size
                        barDataSet.valueTextSize = 16f
                        Log.d(Constants.TAG, "question ans Bar")
                        chart.data = barData
                    }
                    .addOnFailureListener {
                        Log.d(Constants.TAG, "Error$it")
                    }
            }
        hideProgress()

    }
}
