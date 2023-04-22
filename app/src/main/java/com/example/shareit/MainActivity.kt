package com.example.shareit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.shareit.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    private var textToShare = ""
    private var imageUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)



        bind.shareImg.setOnClickListener {
            pickImage()
        }

        bind.btnShareText.setOnClickListener {
            textToShare = bind.shareText.text.toString()
            if(textToShare.isEmpty())
            {
                showToast("Введите текст")
            }else{
                shareText()
            }
        }
        bind.btnshareImg.setOnClickListener {
            if(imageUri ==null){
                showToast("Картинку выбери, чурка!")
            }else{
                shareImage()
            }
        }
        bind.btnShareAll.setOnClickListener {

        }
    }

    private fun shareImage() {
        val contentUri = getContentUri()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Текст из приложения SHAREit")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Выберите приложение, уважаемый чурка:"))
    }

    private fun getContentUri(): Uri {
        var bitmap: Bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            bitmap = ImageDecoder.decodeBitmap(source)
        }else{
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }
        val imageFolder = File(cacheDir, "images")
        var contentUri: Uri? = null
        try {
            imageFolder.mkdir()
            val file = File(imageFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            contentUri = FileProvider.getUriForFile(this, "com.example.shareit.fileprovaider",file)
        }catch (e:java.lang.Exception){
            showToast("${e.message}")
        }
        return contentUri!!
    }

    private fun shareText() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Текст из приложения SHAREit")
        startActivity(Intent.createChooser(intent, "Выберите приложение, уважаемый чурка:"))
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }
    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            imageUri = intent!!.data
            bind.shareImg.setImageURI(imageUri)
        }else{
            showToast("Картинку выбрать не вариант!")
        }
    }
}