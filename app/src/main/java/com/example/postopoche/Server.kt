package com.example.android01
import android.os.Build
import okhttp3.*
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.postopoche.runOnUiThread
import java.io.IOException
import java.util.regex.Pattern


class Api(){
    private val ser = ServerApi()

    var decoded: String =""

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun sender(): String {
        get("get_all_recipes"){ result ->
            if (result.isNotBlank()) {
                try {
                    val decodeds = result.replace("\\u", "\\u")
                        .let { Pattern.compile("\\\\u([0-9A-Fa-f]{4})").matcher(it) }
                        .replaceAll { matchResult ->
                            Integer.parseInt(matchResult.group(1), 16).toChar().toString()
                        }
                    this.decoded = result



                } catch (e: Exception) {

                }
            }
            else{

            }
        }
        return decoded
    }



    fun get(route: String,onResult: (String) -> Unit) {
        ser.get(
            route = route,
            //params = mapOf("username" to "NakoLox", "password" to "Neko12")
        ) { result ->
            runOnUiThread {
                onResult(result) // передаём результат наружу
            }
        }
    }
    fun post(event: String,text:String,atribute: String, onResult: (String) -> Unit) {
        ser.post(
            route = event,
            params = mapOf("username" to text, "password" to atribute)
        ) { result ->
            runOnUiThread {
                onResult(result) // передаём результат наружу
            }
        }
    }
}
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


