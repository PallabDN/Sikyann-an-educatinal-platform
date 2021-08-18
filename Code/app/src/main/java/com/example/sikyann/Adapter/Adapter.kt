package com.example.sikyann.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.QuestionActivity
import com.example.sikyann.Activity.QuestionPaperActivity
import com.example.sikyann.Activity.SpecificQuestionPaperActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.Module.StudentResltModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.single.view.*

class Adapter(private val mutableList: MutableList<QuestionPaperModel>,
              private val mutableListDate: MutableList<String>,
              private val mutableListDocId:MutableList<String>,
              private val context: Context,
              private val clazz : Class<SpecificQuestionPaperActivity>,
              private val currentUser:String = ""
): RecyclerView.Adapter<QuestionPaperActivity.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionPaperActivity.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single,parent,false)
        return QuestionPaperActivity.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionPaperActivity.ViewHolder, position: Int) {
        val q = mutableList[position]
        val date = mutableListDate[position]
        holder.itemView.titleQuestion.text = q.title
        holder.itemView.subtitleQuestion.text = q.subtitle

        holder.itemView.copy_code.visibility = View.GONE
        holder.itemView.date.text = date
        holder.itemView.mark.text = "Marks: ${q.totalMark}"

        val s = q.uniqueId
        val pattern = Regex("\\+")
        var list = mutableListOf<String>()
        list = pattern.split(s.toString()) as MutableList<String>

        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.USER).document(list[0])
            .get().addOnSuccessListener {
                val user = it.toObject(UserDetails::class.java)!!
                holder.itemView.createdBy.text = "By: ${user.name}"
            }
        db.collection(Constants.USER).document(currentUser).collection(Constants.QUESTION_PAPER).document(mutableListDocId[position])
            .get().addOnSuccessListener {
                val result = it.toObject(StudentResltModel::class.java)!!
                holder.itemView.time.visibility = View.VISIBLE
                holder.itemView.time.text = result.time
            }


        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.QUESTION_ID,mutableList[position].uniqueId)
            intent.putExtra(Constants.DOC_ID,mutableListDocId[position])
            ContextCompat.startActivity(context,intent,null)
        }



    }

    override fun getItemCount(): Int {
        return mutableList.size
    }
}