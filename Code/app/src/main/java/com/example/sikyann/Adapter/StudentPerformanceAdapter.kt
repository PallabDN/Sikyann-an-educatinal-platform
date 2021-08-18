package com.example.sikyann.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.StudentPerformanceActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.StudentResltModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_student_performance.view.*
import kotlinx.android.synthetic.main.student_performance.view.*
import java.util.*

class StudentPerformanceAdapter(
    private val mutableList: MutableList<StudentResltModel>,
    private val answerList: MutableList<String>,
    private val markList: MutableList<String>
): RecyclerView.Adapter<StudentPerformanceActivity.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentPerformanceActivity.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_performance,parent,false)
        return StudentPerformanceActivity.viewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentPerformanceActivity.viewHolder, position: Int) {
        val x = mutableList[position]
        holder.itemView.performance_student_time.text = x.time
        holder.itemView.performance_student_date.text = x.date

        val barList = mutableListOf<BarEntry>()
        FirebaseFirestore.getInstance().collection(Constants.USER).document(x.id.toString())
            .get().addOnSuccessListener {
                val student = it.toObject(UserDetails::class.java)!!
                holder.itemView.performance_student_name.text = student.name
                holder.itemView.performance_student_gmail.text = student.gmail
            }
        for (i in answerList.indices){
            if(answerList[i].toString().toLowerCase(Locale.ROOT) == x.result?.get(i)!!.toLowerCase(Locale.ROOT)){
                barList.add(BarEntry((i+1).toFloat(),markList[i].toFloat()))
            }else{
                barList.add(BarEntry((i+1).toFloat(),0f))
            }
        }
        val barDataset = BarDataSet(barList,"Result")
        val barData = BarData(barDataset)
        // adding color to our bar data set.
        barDataset.setColors(Color.BLUE)
        // setting text color.
        barDataset.valueTextColor = Color.BLACK
        // setting text size
        barDataset.valueTextSize = 16f
        holder.itemView.performance_student_Barchart.data = barData

    }

    override fun getItemCount(): Int {
        return mutableList.size
    }
}