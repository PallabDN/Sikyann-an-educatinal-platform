package com.example.sikyann.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.sikyann.Constants
import com.example.sikyann.Module.ClassRoomContentModel
import com.example.sikyann.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_specific_class_room.*
import kotlinx.android.synthetic.main.activity_upload_class_room_content.*


class UploadClassRoomContentActivity : RootActivity() {


    val db = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = storage.reference
    var pdf = true
    var image = true
    var resourceType:String = ""

    var downloadUri : String? = null

    var ref:String? = null

    val uid = uniqueID()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_class_room_content)

        if(!isNetworkActive(this@UploadClassRoomContentActivity)){
            showSnackbar(getString(R.string.network),upload_classRoom_content_view)
            upload_classRoom_content_view.visibility = View.GONE
        }

        val id = intent.getStringExtra(Constants.CLASSROOM_ID)
        val pattern = Regex("\\+")
        val list = pattern.split(id.toString()) as MutableList
        uploadClassroomPDF.setOnClickListener {
            if(pdf && image){
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "application/pdf"
                startActivityForResult(intent, Constants.PICK_PDF_REQUEST)

            }else{
                Toast.makeText(this@UploadClassRoomContentActivity,"Already Uploaded",Toast.LENGTH_SHORT).show()
            }
        }

        uploadClassroomImage.setOnClickListener {
            if(pdf && image){
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent,Constants.PICK_IMAGE_REQUEST)
            }else{
                Toast.makeText(this@UploadClassRoomContentActivity,"Already Uploaded",Toast.LENGTH_SHORT).show()
            }
        }

        content_pdf.setOnClickListener {
            val intent = Intent(this@UploadClassRoomContentActivity,ViewPDFActivity::class.java)
            intent.putExtra(Constants.PDF,downloadUri)
            startActivity(intent)
        }

        createContentFinish.setOnClickListener {
            if(content_title.text.toString().isNotEmpty()){
                showProgress()
                val data =
                    ClassRoomContentModel(content_title.text.toString(),content_description.text.toString()
                        ,content_resource_link.text.toString(),downloadUri,resourceType,getDate(),getTime())

                db.collection(Constants.TEACHER).document(currentUser()).collection(Constants.CLASS_ROOM).document(list[1])
                    .collection(Constants.CONTENTS).add(data).addOnSuccessListener {
                        Log.d(Constants.TAG,"Successfully content added")
                    }
                    .addOnFailureListener {
                        Log.d(Constants.TAG,"Error  $it")
                    }
                hideProgress()
                finish()
            }else{
                Toast.makeText(this,"Give tittle of Content",Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val imageURI = data!!.data

            val filepath: StorageReference = if(requestCode==Constants.PICK_PDF_REQUEST){
                pdf = false
                ref = "classroom_content/" + currentUser() + "/"+"PDF"+"/"+ uid
                storageReference.child(ref!!)
            }else{
                image = false
                ref = "classroom_content/" + currentUser() + "/"+ "Image"+"/"+ uid
                storageReference.child(ref!!)
            }


            val uploadTask = filepath.putFile(imageURI!!).addOnSuccessListener {
                progressDialog.dismiss()
                Toast
                    .makeText(
                        this@UploadClassRoomContentActivity,
                        "Content Uploaded!!",
                        Toast.LENGTH_SHORT
                    )
                    .show()


            }.addOnFailureListener{e->
                progressDialog.dismiss()
                Toast
                    .makeText(
                        this@UploadClassRoomContentActivity,
                        "Failed " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
            }

            uploadTask.continueWithTask{task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                filepath.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    downloadUri = it.result!!.toString()

                    updateUI()
                }
            }


        }
    }




    fun updateUI(){
        if(!image){
            content_image.visibility = View.VISIBLE
            resourceType = Constants.IMAGE
            Glide
                .with(this@UploadClassRoomContentActivity)
                .load(downloadUri)
                .fitCenter()
                .into(content_image);
        }
        if(!pdf){
            content_pdf.visibility = View.VISIBLE
            resourceType = Constants.PDF
        }
    }

    override fun onBackPressed() {
        showProgress()
        if(!pdf){
            storageReference.child(ref!!).delete()
                .addOnFailureListener {
                    Log.d(Constants.TAG,"Successfully deleted")
                }.addOnFailureListener {
                    Log.d(Constants.TAG,it.toString())
                }
        }
        if(!image){
            storageReference.child(ref!!).delete()
                .addOnSuccessListener {
                    Log.d(Constants.TAG,"Successfully deleted")
                }
                .addOnFailureListener {
                    Log.d(Constants.TAG,it.toString())
                }
        }
        finish()
        hideProgress()
    }




}














