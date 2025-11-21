package com.example.postopoche

import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("description")
        val imgBase64 = intent.getStringExtra("imageBase64")



    }
}
