package com.example.postopoche

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android01.ToPy
import com.example.postopoche.ProductAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import kotlinx.coroutines.*

class Py2 {
    private val toPy = ToPy()
    var lastResponse: String? = null
        private set

    fun sender(event: String, text: String, attribute: String, onResponse: (String) -> Unit) {
        // Используем глобальную область корутин
        CoroutineScope(Dispatchers.IO).launch {
            val response = toPy.sendAndWait(event, text, attribute)
            lastResponse = response
            withContext(Dispatchers.Main) {
                onResponse(response ?: "")
            }
        }
    }
}


class Logining : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logining)

        intent.putExtra("message", "Вы перешли в окно Logining 🚀")

        val buttonGo: Button = findViewById(R.id.buttonGo)
        val buttonEnter: Button = findViewById(R.id.buttonEnter)
        val imageView9: ImageView = findViewById(R.id.imageView9)



        buttonGo.setOnClickListener {
            val intent = Intent(this, Registing::class.java)
            startActivity(intent)
        }

        buttonEnter.setOnClickListener {


            imageView9.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView9.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()

            val py = Py2()

            val name = findViewById<EditText>(R.id.name).text.toString()
            val pass = findViewById<EditText>(R.id.pass).text.toString()

            py.sender("log", name, pass) { response ->
                // Просто показываем то, что вернул сервер
                Toast.makeText(this, "Ответ: $response", Toast.LENGTH_LONG).show()

                intent.putExtra("message", response)

            }
        }


    }
}
