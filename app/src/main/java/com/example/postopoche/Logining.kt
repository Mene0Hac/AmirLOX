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
import com.example.android01.Api
import org.json.JSONObject
import kotlinx.coroutines.*
import com.google.gson.Gson

var seePass = false
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


data class RegisterResponse(
    val message: String,
    val status_code: Int,
    val token: String,
    val username: String
)

class Logining : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logining)


        intent.putExtra("message", "Вы перешли в окно Logining 🚀")

        val buttonGo: Button = findViewById(R.id.buttonGo)
        val buttonEnter: Button = findViewById(R.id.buttonEnter)
        val imageView9: ImageView = findViewById(R.id.imageView9)
        val imageView13: ImageView = findViewById(R.id.imageView13)

        var pass = findViewById<EditText>(R.id.pass)
        val button4: Button = findViewById(R.id.button4)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        button4.setOnClickListener {

            imageView13.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView13.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .withEndAction {
                            if (seePass) {
                                // Делаем пароль невидимым
                                pass.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                                seePass= false
                                imageView13.setImageResource(R.drawable.password2)
                            } else {
                                // Делаем пароль видимым
                                pass.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                seePass = true
                                imageView13.setImageResource(R.drawable.password)

                            }


                        }
                        .start()
                }
                .start()


        }

        buttonGo.setOnClickListener {
            val intent = Intent(this, Registering::class.java)
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
                        .withEndAction {
                            val name = findViewById<EditText>(R.id.name)
                            pass = findViewById<EditText>(R.id.pass)
                            val api = Api()
                            val gson = Gson()
                            val params = gson.toJson(mapOf("username" to name.text.toString(), "password" to pass.text.toString()))
                            api.post("login_user","",params){ result ->
                                if (result.isNotBlank()) {
                                    try {
                                        println("999"+result)
                                        val obj = JSONObject(result)
                                        val token = obj.getString("token")
                                        val username = obj.getString("username")
                                        val status = obj.getInt("status_code")

                                        if (status.toString()=="200"){
                                            val set = Settings(this)
                                            set.username=username
                                            set.token=token
                                            Toast.makeText(this, set.username, Toast.LENGTH_SHORT).show()
                                            set.save()
                                            onBackPressedDispatcher.onBackPressed()
                                        }else{
                                            Toast.makeText(this, "Необработанный ответ", Toast.LENGTH_SHORT).show()
                                        }


                                    } catch (e: Exception) {
                                        if (e.toString()=="org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject"){
                                            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                                        }else if(e.toString()=="org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject"){
                                            Toast.makeText(this, "Сервер не отвечает", Toast.LENGTH_SHORT).show()
                                        }else {
                                            Toast.makeText(this, "!" + e, Toast.LENGTH_SHORT).show()
                                            println("!" + e)
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(this, "44", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                        .start()
                }
                .start()


        }


    }
}
