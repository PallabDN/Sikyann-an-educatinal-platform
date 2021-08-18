package com.example.sikyann.Adapter

import android.R.attr.label
import android.app.PendingIntent.getActivity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.RootActivity
import com.example.sikyann.Activity.SpecificQuestionPaperActivity
import com.example.sikyann.Activity.TeacherQuestPapersActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.QuestionPaperModel
import com.example.sikyann.R
import kotlinx.android.synthetic.main.single.view.*


class TeacherQuestionPaperAdapter(
    val mutableList: MutableList<QuestionPaperModel>,
    val context: Context,
    val clazz: Class<SpecificQuestionPaperActivity>
): RecyclerView.Adapter<TeacherQuestPapersActivity.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherQuestPapersActivity.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single, parent, false)
        return TeacherQuestPapersActivity.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherQuestPapersActivity.ViewHolder, position: Int) {
        val q = mutableList[position]
        holder.itemView.titleQuestion.text = q.title
        holder.itemView.subtitleQuestion.text = q.subtitle
        holder.itemView.code.text = q.uniqueId
        holder.itemView.copy.setOnClickListener {
            val clipboard:ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", q.uniqueId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context,"Code copied",Toast.LENGTH_SHORT).show()
        }
        holder.itemView.createdBy.visibility = View.GONE
        holder.itemView.date.text = q.date
        holder.itemView.mark.text = "Marks: ${q.totalMark}"
        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.QUESTION_ID,mutableList[position].uniqueId)
            intent.putExtra(Constants.DOC_ID,"")
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

}