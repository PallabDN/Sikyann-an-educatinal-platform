package com.example.sikyann.Activity

import android.R.attr.label
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.sikyann.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


open class RootActivity:AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkActive(context: Context): Boolean{
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if(capabilities!=null){
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                return true
            }else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                return true
            }
            else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                return true
            }
        }
        return false
    }

    fun showSnackbar(s: String, view: View){
        val snackbar = Snackbar.make(view, s, Snackbar.LENGTH_INDEFINITE).setAction("Retry") {
            startActivity(intent)
            finish()
        }
            .setActionTextColor(Color.WHITE)


        val snakbarView = snackbar.view
        snakbarView.setBackgroundColor(Color.BLACK);

        val textView = snakbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        textView.setTextColor(Color.WHITE)
        textView.textSize = 10f
        snackbar.show()
    }

    fun showProgress(){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_bar)
        mProgressDialog.show()
    }

    fun hideProgress(){
        mProgressDialog.dismiss()
    }

    fun currentUser():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun uniqueID():String{
        return UUID.randomUUID().toString()
    }

    fun getDate():String{
        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/YYYY")
        return dateFormat.format(calender.time)
    }

    fun getTime():String{
        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(calender.time)
    }

    fun CopyToClipboard(a: String){
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), a)
        clipboard.setPrimaryClip(clip)
    }


}