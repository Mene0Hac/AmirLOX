package com.example.postopoche

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

class Registing : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name = findViewById<EditText>(R.id.name).text.toString()
        val pass = findViewById<EditText>(R.id.pass).text.toString()
        val pass2 = findViewById<EditText>(R.id.pass2).text.toString()
        val imageView9: ImageView = findViewById(R.id.imageView9)
        val buttonGo: Button = findViewById(R.id.buttonGo)

        buttonGo.setOnClickListener {
            imageView9.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView9.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .withEndAction {
                            if (pass==pass2){
                            }else{
                                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .start()
                }
                .start()

        }
    }
}