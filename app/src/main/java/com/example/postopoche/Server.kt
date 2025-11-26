package com.example.android01
import okhttp3.*
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException



class ServerApi {

    private val client = OkHttpClient()
    private val url= "http://5.182.87.105:5001/"

    fun post(
        route: String,
        params: Map<String, String>,
        callback: (String) -> Unit
    ) {
        val curl=this.url+route
        val formBodyBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBodyBuilder.add(key, value)
        }
        val formBody = formBodyBuilder.build()

        val request = Request.Builder()
            .url(curl)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Ошибка: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string() ?: "Нет ответа")
                } else {
                    callback("Ошибка сервера")
                }
            }
        })
    }

    fun get(
        route: String,
        callback: (String) -> Unit
    ) {
        val curl=this.url+route
        val request = Request.Builder()
            .url(curl)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Ошибка: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string() ?: "Нет ответа")
                } else {
                    callback("Ошибка сервера")
                }
            }
        })
    }
}


