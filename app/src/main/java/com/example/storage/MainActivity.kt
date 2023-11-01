package com.example.storage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import io.appwrite.Client
import io.appwrite.services.Storage

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var storageService: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = Client(context)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("6542c5c5aecbcce8cea6")
            .setSelfSigned(status: true)

        storageService = Storage(client)

        imageView = findViewById(R.id.imageView3)
        val selectButton: Button = findViewById(R.id.btactualizar)

        imageView.setOnClickListener {
            openGallery()
        }

        selectButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)

            // Subir la imagen a Appwrite Storage
            selectedImageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val file = File("filename.jpg") // Reemplaza con el nombre deseado para el archivo
                inputStream?.copyTo(FileOutputStream(file))
                inputStream?.close()

                val uploadTask = storageService.createFile(file) { result ->
                    if (result.isSuccess) {
                        val fileId = result.body?.getString("fileId")
                        // Ahora puedes utilizar fileId para hacer referencia al archivo en Appwrite Storage
                    } else {
                        // Handle error
                    }
                }
            }
        }
    }
}}