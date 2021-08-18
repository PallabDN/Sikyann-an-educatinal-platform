package com.example.sikyann.Adapter

import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.sikyann.Activity.ClassRoomMembersActivity
import com.example.sikyann.Module.StudentClassRoomDataModel
import com.example.sikyann.Module.UserDetails
import com.example.sikyann.R
import kotlinx.android.synthetic.main.custom_dailoge.*
import kotlinx.android.synthetic.main.members.view.*

class ClassRoomMemberAdapter(
    val mutableList: MutableList<StudentClassRoomDataModel>,
    val mutableListStudent: MutableList<UserDetails>,
    val context: Context,
) : RecyclerView.Adapter<ClassRoomMembersActivity.viewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassRoomMembersActivity.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.members,parent,false)
        return ClassRoomMembersActivity.viewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassRoomMembersActivity.viewHolder, position: Int) {
        holder.itemView.classRoomMemeberName.text = mutableListStudent[position].name
        holder.itemView.memberJoinDate.text = mutableList[position].date

        holder.itemView.setOnClickListener {

            val custom = Dialog(context)
            custom.setContentView(R.layout.custom_dailoge)
            custom.memberName.text = mutableListStudent[position].name
            custom.memberGmail.text = mutableListStudent[position].gmail
            custom.memberMobile.text = mutableListStudent[position].mobile
            custom.memberFather.text = mutableListStudent[position].father
            custom.memberAddress.text = mutableListStudent[position].address
            custom.show()
        }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }
}