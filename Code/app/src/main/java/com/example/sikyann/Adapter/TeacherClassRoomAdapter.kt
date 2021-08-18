package com.example.sikyann.Adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.SpecificClassRoomActivity
import com.example.sikyann.Activity.TeacherClassRoomsActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomModel
import com.example.sikyann.R
import kotlinx.android.synthetic.main.single.view.*

class TeacherClassRoomAdapter(
    val mutableList: MutableList<ClassRoomModel>,
    val context: Context,
    private val clazz : Class<SpecificClassRoomActivity>
) : RecyclerView.Adapter<TeacherClassRoomsActivity.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherClassRoomsActivity.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single,parent,false)
        return TeacherClassRoomsActivity.viewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherClassRoomsActivity.viewHolder, position: Int) {
        val p = mutableList[position]
        holder.itemView.titleQuestion.text = p.title
        holder.itemView.subtitleQuestion.text = p.subtitle
        holder.itemView.code.text = p.uniqueId
        holder.itemView.copy.setOnClickListener {
            val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", p.uniqueId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context,"Code copied", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.date.text = p.date
        holder.itemView.mark.visibility=View.GONE
        holder.itemView.createdBy.visibility = View.GONE


        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.CLASSROOM_ID,p.uniqueId)
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return mutableList.size
    }
}