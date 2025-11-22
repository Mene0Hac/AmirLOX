package com.example.postopoche

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android01.ToPy
import android.content.Context
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.*
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.android01.ServerApi
import com.example.postopoche.MainActivity.Product

import org.json.JSONArray
import org.json.JSONObject



class LocalData {


    val defaultProducts = mutableListOf(
        Product("Яблоко", "Свежее красное яблоко"),
        Product("Банан", "Спелый жёлтый банан"),
        Product("Апельсин", "Сочный апельсин, богатый витамином C"),
        Product("Виноград", "Сладкий зелёный виноград"),
        Product("Груша", "Ароматная спелая груша"),
        Product("Киви", "Мягкий фрукт с кисло-сладким вкусом"),
        Product("Ананас", "Сладкий тропический ананас"),
        Product("Арбуз", "Сочный летний арбуз"),
        Product("Клубника", "Сладкая спелая клубника"),
        Product("Черника", "Полезная лесная ягода"),
        Product("Мандарин", "Сладкий цитрусовый фрукт без косточек"),
        Product("Манго", "Мякоть нежная и сладкая, с ароматом тропиков"),
        Product("Персик", "Сочный фрукт с бархатной кожурой"),
        Product("Слива", "Спелая слива с кисло-сладким вкусом"),
        Product("Дыня", "Ароматная дыня, идеальна для жары")
    )

    val fruits = listOf(
        MainActivity.Product("Яблоко", "С1вежее красное яблоко"),
        MainActivity.Product("Банан", "Жёлтый и сладкий","","")
    )

    val vegetables = listOf(
        MainActivity.Product("Морковь", "Полезная и хрустящая","",""),
        MainActivity.Product("Огурец", "Свежий и зелёный","","")
    )

    val drinks = listOf(
        MainActivity.Product("Сок", "Апельсиновый, натуральный","",""),
        MainActivity.Product("Кофе", "Ароматный и бодрящий","","")
    )


}

class Py {
    private val toPy = ToPy()
    var lastResponse: String? = null
        private set


    fun sender(onResponse: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = toPy.sendAndWait("query", "mene", "228")
            lastResponse = response
            withContext(Dispatchers.Main) {
                onResponse(response ?: "")
            }
        }
    }
}

class Api(){
    private val ser = ServerApi()



    fun post(onResult: (String) -> Unit) {
        ser.post(
            route = "test",
            params = mapOf("username" to "NakoLox", "password" to "Neko12")
        ) { result ->
            runOnUiThread {
                onResult(result) // ← передаём результат наружу
            }
        }
    }

}
fun runOnUiThread(action: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        action()
    }
}




fun parseServerResponse(response: String): List<MainActivity.Product> {
    val regex = Regex("""Product\("([^"]+)",\s*"([^"]+)",\s*"([^"]*)",\s*"([^"]*)"\)""")
    return regex.findAll(response).map { match ->
        val (name, desc, base64, recipe) = match.destructured
        MainActivity.Product(name, desc, base64, recipe)
    }.toList()
}




class MainActivity : AppCompatActivity() {

    data class Product(
        val name: String,
        val description: String,
        val imageBase64: String? = null,
        val recipe: String? = "Рецепта нет!"
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val button1: Button = findViewById(R.id.button3)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button1)
        val buttonReg: Button = findViewById(R.id.buttonReg)
        val imageView: ImageView = findViewById(R.id.imageView)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val imageView3: ImageView = findViewById(R.id.imageView3)
        val imageView5: ImageView = findViewById(R.id.imageView5)

        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(mutableListOf())
        recyclerView.adapter = adapter

        adapter.initFavorites(this)       // ← грузим избранное ОДИН раз
        adapter.updateProducts(LocalData().defaultProducts)

        var data = LocalData()


        button1.setOnClickListener {
            // --- Анимация ImageView ---
            imageView2.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView2.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()

            val py = Py()
            py.sender { response ->
                if (response.isNotBlank()) {
                    try {
                        val products = parseServerResponse(response)
                        if (products.isNotEmpty()) {
                            runOnUiThread {
                                adapter.updateProducts(products)
                                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this, "Нет корректных данных", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this, "Ошибка парсинга данных", Toast.LENGTH_SHORT).show()
                        }
                        e.printStackTrace()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Пустой ответ от сервера", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        button3.setOnClickListener {

            imageView3.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView3.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()


            val favManager = FavoritesManager(this)
            val saved = favManager.getFavorites()


            if (saved.toString() != "[]"){
                val productList = saved.map {
                    Product(it.name, it.description, it.imageBase64)
                }.toMutableList()

                if (recyclerView.layoutManager == null) {
                    recyclerView.layoutManager = LinearLayoutManager(this)
                }

                adapter.updateProducts(productList)
            }
            else{
                Toast.makeText(this, "Вы пока ничего не добавили в избранное!", Toast.LENGTH_SHORT).show()
            }

        }

        button2.setOnClickListener {

            imageView.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()

            adapter.updateProducts(data.drinks)
            Toast.makeText(this, "☕ Показаны напитки", Toast.LENGTH_SHORT).show()
        }

            //Toast.makeText(this, "est.toString()" , Toast.LENGTH_SHORT).show()
            //fav.getFavorites()
            //adapter.updateProducts(data.drinks)
            //Toast.makeText(this, "☕ Показаны напитки", Toast.LENGTH_SHORT).show()


        buttonReg.setOnClickListener {

            imageView5.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView5.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()

            val intent = Intent(this, Logining::class.java)
            startActivity(intent)
        }




        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



    }


}

