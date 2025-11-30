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
import com.example.android01.Api
import org.json.JSONObject

class Registering : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                            val name = findViewById<EditText>(R.id.name).text.toString()
                            val pass = findViewById<EditText>(R.id.pass).text.toString()
                            val pass2 = findViewById<EditText>(R.id.pass2).text.toString()
                            if (pass==pass2) {
                                println("pass1"+pass+"||"+pass2)
                                Toast.makeText(this, "PassOk", Toast.LENGTH_SHORT).show()
                                val api = Api()
                                api.post("register_user",name,pass){ result ->
                                    if (result.isNotBlank()) {
                                        try {

                                            //Toast.makeText(this, "!@"+result, Toast.LENGTH_SHORT).show()
                                            println("result="+result)

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
                                            }else{
                                                Toast.makeText(this, "!"+status, Toast.LENGTH_SHORT).show()
                                            }


                                        } catch (e: Exception) {
                                            Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                            println("!"+e)
                                        }
                                    }
                                    else{
                                        Toast.makeText(this, "44", Toast.LENGTH_SHORT).show()
                                    }
                                }
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