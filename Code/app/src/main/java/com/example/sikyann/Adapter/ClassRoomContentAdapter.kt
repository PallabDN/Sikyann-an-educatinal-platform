package com.example.sikyann.Adapter

import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sikyann.Activity.SpecificClassRoomActivity
import com.example.sikyann.Activity.ViewPDFActivity
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomContentModel
import com.example.sikyann.R
import kotlinx.android.synthetic.main.content_of_classroom.view.*

class ClassRoomContentAdapter(
    val mutableList: MutableList<ClassRoomContentModel>,
    val context: Context,
    val clazz: Class<ViewPDFActivity>
) : RecyclerView.Adapter<SpecificClassRoomActivity.viewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpecificClassRoomActivity.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_of_classroom,parent,false)
        return SpecificClassRoomActivity.viewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecificClassRoomActivity.viewHolder, position: Int) {
        val p = mutableList[position]
        holder.itemView.classroom_content_title.text = p.title
        holder.itemView.content_create_time.text = p.time
        holder.itemView.content_create_date.text = p.date
        if(p.description!!.isEmpty()){
            holder.itemView.content_description.visibility = View.GONE
        }else{
            holder.itemView.content_description.text = p.description
        }
        if(p.link!!.isNotEmpty()){
            holder.itemView.content_link.visibility = View.VISIBLE
            holder.itemView.content_link.text = p.link
            holder.itemView.content_link.movementMethod = LinkMovementMethod.getInstance()
        }else{
            holder.itemView.content_link.visibility = View.GONE
        }

        if(p.resourceType==Constants.PDF){
            holder.itemView.content_pdf.visibility = View.VISIBLE
        }
        if(p.resourceType==Constants.IMAGE){
            holder.itemView.content_image.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(p.resource)
                .fitCenter()
                .into(holder.itemView.content_image);
        }

        holder.itemView.content_pdf.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.PDF,p.resource)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }


}