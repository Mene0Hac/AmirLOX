package com.example.postopoche

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetails : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)   // <-- ВАЖНО!

        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("description")
        val imgBase64 = intent.getStringExtra("imageBase64")
        val recipe =  intent.getStringExtra("recipe")
        val rating =  intent.getStringExtra("rating")
        val calories =  intent.getStringExtra("calories")
        val avtor =  intent.getStringExtra("avtor")
        val products =  intent.getStringExtra("products")

        val textView3: TextView = findViewById(R.id.textView3)
        textView3.text = "!"+products+"!"

        if (!imgBase64.isNullOrEmpty()) {

            val cleanBase64 = imgBase64.substringAfter(",")
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            val img = findViewById<ImageView>(R.id.imageView12)
            img.setImageBitmap(bitmap)
        }
    }
}

