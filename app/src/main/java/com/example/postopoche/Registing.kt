package com.example.postopoche

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class Registing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        intent.putExtra("message", "Вы перешли в окно reg🚀")
    }
}