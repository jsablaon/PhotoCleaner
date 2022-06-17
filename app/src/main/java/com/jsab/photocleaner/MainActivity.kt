package com.jsab.photocleaner

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var image_holder: ImageView
    lateinit var camera_btn: ImageButton
    lateinit var library_btn: ImageButton
    val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image_holder = findViewById(R.id.image_holder)


        library_btn = findViewById<ImageButton>(R.id.libraryButton)
        library_btn.setOnClickListener{
            var intent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(intent)
            finish()
        }

        camera_btn = findViewById<ImageButton>(R.id.cameraButton)
        camera_btn.setOnClickListener{
            println("++++++++++++++++++++++++++++++++camera_btn++++++++++++++++++++++++++++")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (err: Exception){
                Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            image_holder.setImageBitmap(imageBitmap)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}