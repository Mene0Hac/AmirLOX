package com.example.postopoche

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


fun decodeBase64Safe(base64: String, reqWidth: Int, reqHeight: Int): Bitmap? {
    return try {
        val clean = base64.substringAfter(",")
        val bytes = Base64.decode(clean, Base64.DEFAULT)

        // 1. Сначала считываем только размеры, без загрузки bitmap
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        // 2. Вычисляем коэффициент уменьшения
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // 3. Теперь декодируем картинку с уменьшением
        options.inJustDecodeBounds = false
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

    } catch (e: Exception) {
        null
    }
}



fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

class ProductDetails : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)   // <-- ВАЖНО!

        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("description")

        val key = intent.getStringExtra("key")
        val imgBase64 = key?.let { ImageBase64Cache.get(it) }


        //val imgBase64 = intent.getStringExtra("imageBase64")
        val recipe =  intent.getStringExtra("recipe")
        val rating =  intent.getStringExtra("rating")
        val calories =  intent.getStringExtra("calories")
        val avtor =  intent.getStringExtra("avtor")
        val id  =  intent.getStringExtra("id")
        val products =  intent.getStringExtra("products")

        val textView3: TextView = findViewById(R.id.textView3)
        textView3.text = "!"+products+"!"

        if (!imgBase64.isNullOrEmpty()) {

            val bitmap = decodeBase64Safe(imgBase64, 800, 800) // ограничиваем размер

            if (bitmap != null) {
                val img = findViewById<ImageView>(R.id.imageView12)
                img.setImageBitmap(bitmap)
            }
        }

    }
}

