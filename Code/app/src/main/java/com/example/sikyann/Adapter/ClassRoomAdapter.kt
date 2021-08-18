package com.example.sikyann.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.ClassRoomsActivity
import com.example.sikyann.Activity.QuestionActivity
import com.example.sikyann.Activity.QuestionPaperActivity
import com.example.sikyann.Activity.SpecificClassRoomActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.Module.QuestionModel
import com.example.sikyann.Module.StudentClassRoomDataModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.single.view.*

class ClassRoomAdapter(private val mutableList: MutableList<ClassRoomModel>,
                       private val mutableListOfClassRoom: MutableList<StudentClassRoomDataModel>,
                       private val context: Context,
                       private val clazz : Class<SpecificClassRoomActivity>):
    RecyclerView.Adapter<ClassRoomsActivity.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassRoomsActivity.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single,parent,false)
        return ClassRoomsActivity.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassRoomsActivity.ViewHolder, position: Int) {
        val q = mutableList[position]
        val p = mutableListOfClassRoom[position]
        holder.itemView.titleQuestion.text = q.title
        holder.itemView.subtitleQuestion.text = q.subtitle
        holder.itemView.copy_code.visibility = View.GONE
        holder.itemView.date.text = p.date
        holder.itemView.mark.visibility = View.GONE
        val s = q.uniqueId!!
        val pattern = Regex("\\+")
        var list = mutableListOf<String>()
        list = pattern.split(s) as MutableList<String>

        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.USER).document(list[0])
            .get().addOnSuccessListener {
                val user = it.toObject(UserDetails::class.java)!!
                holder.itemView.createdBy.text = "By: ${user.name}"
            }

        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.CLASSROOM_ID,q.uniqueId)
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
       return mutableList.size
    }
}