package com.example.sikyann.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import com.example.sikyann.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : RootActivity(){

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        if(!isNetworkActive(this@RegisterActivity)){
            showSnackbar(getString(R.string.network),register_view)
            register_view.visibility = View.GONE
        }else{
            register_as_teacher.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, TeacherRegisterActivity::class.java))
                finish()
            }
            register_as_student.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, StudentActivityRegister::class.java))
                finish()
            }
        }


    }
}
