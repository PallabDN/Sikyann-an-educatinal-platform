package com.example.sikyann.Activity

import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sikyann.Constants
import com.example.sikyann.R
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view_p_d_f.*
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection


class ViewPDFActivity : RootActivity() {


    private var pDialog: ProgressDialog? = null
    val progress_bar_type = 0

    lateinit var pdfView: PDFView
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_p_d_f)

        pdfView = findViewById(R.id.pdf)

        if(!isNetworkActive(this@ViewPDFActivity)){
            showSnackbar(getString(R.string.network), view_pdf_view)
        }

        val url = intent.getStringExtra(Constants.PDF)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        RetrivePdfStream(pdfView).execute(url)
        progressDialog.dismiss()

        val download = findViewById<FloatingActionButton>(R.id.download_pdf)
        download.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this@ViewPDFActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                Log.d(Constants.TAG, url.toString())
                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val httpReference = storage.getReferenceFromUrl(url.toString())
                httpReference.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    downloadFile(url,this@ViewPDFActivity)
                }.addOnFailureListener {
                    Toast.makeText(this@ViewPDFActivity, "Something Wrong", Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.toString())
                }
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.STORAGE_PERMISSION
                )
            }



        }

    }

    // Retrieving the pdf file using url
    class RetrivePdfStream(val x: PDFView): AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg params: String?): InputStream {
            var inputStream: InputStream? = null

            val url = URL(params[0])
            val urlConnection = url.openConnection() as HttpURLConnection
            if(urlConnection.responseCode == 200){
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
            return inputStream!!
        }

        override fun onPostExecute(result: InputStream?) {
            x.fromStream(result).load()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Constants.STORAGE_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "I have permission", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(
                    this,
                    "Sorry you did not give permission of storage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }




    fun downloadFile(url:String,context: Context){
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context,"Sikyann", "resource.pdf")
        downloadManager.enqueue(request)

    }





}
