package com.example.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    var messageDisplayView : TextView? =null
    var snapDisplayView : ImageView? = null
    val mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageDisplayView = findViewById(R.id.messageDisplayView)
        snapDisplayView = findViewById(R.id.snapDisplayView)

        messageDisplayView?.text = intent.getStringExtra("message")

        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage =
                task.execute(intent.getStringExtra("imageURL"))
                    .get()!!
            snapDisplayView?.setImageBitmap(myImage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloader : AsyncTask<String?, Void?, Bitmap?>() {
         override fun doInBackground(vararg p0: String?): Bitmap? {
            return try {
                val url = URL(p0[0])
                val connection =
                    url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

        //to delete the snap after it is seen and back button is pressed
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid)
            .child("snaps").child(intent.getStringExtra("snapKey")!!).removeValue()

        FirebaseStorage.getInstance().getReference().child("images")
            .child(intent.getStringExtra("imageName")!!).delete()
        //this code removes the data and image from database and storage but the snaps listview does not
        //get updated do we use onChildRemoved() in snaps activity
    }
}