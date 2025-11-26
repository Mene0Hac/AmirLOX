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

import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.view.Gravity
import androidx.core.view.GravityCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Lifecycle

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern


class SreverState(){
    var idname: String = ""
}

data class ServerResponse(
    val data: List<ProductJson>
)

data class ProductJson(
    val id: Int,
    val title: String,
    val description: String,
    val description_of_cooking_process: String,
    val caloric_content: Int,
    val rating: Double,
    val author: AuthorJson,
    val ingredients: List<IngredientJson>
)
data class AuthorJson(
    val id: Int,
    val username: String,
    val is_admin: Boolean,
    val is_banned: Boolean
)

data class IngredientJson(
    val id: Int,
    val title: String,
    val amount: String,
    val description: String
)

class Settings(private val context: Context) {
    var seeKalories: Boolean = true
    var seeReting: Boolean = true
    var BigLook: Boolean = false

    private val prefs = context.getSharedPreferences("my_settings", Context.MODE_PRIVATE)

    fun save() {
        prefs.edit().apply {
            putBoolean("seeKalories", seeKalories)
            putBoolean("seeReting", seeReting)
            putBoolean("BigLook", BigLook)
            apply()
        }
    }

    fun load() {
        seeKalories = prefs.getBoolean("seeKalories", true)
        seeReting = prefs.getBoolean("seeReting", true)
        BigLook = prefs.getBoolean("BigLook", true)
    }
}




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
        Product("Яблоко", "С1вежее красное яблоко"),
        Product("Банан", "Жёлтый и сладкий","","")
    )

    val vegetables = listOf(
        Product("Морковь", "Полезная и хрустящая","",""),
        Product("Огурец", "Свежий и зелёный","","")
    )

    val drinks = listOf(
        Product("Сок", "Апельсиновый, натуральный","",""),
        Product("Кофе", "Ароматный и бодрящий","","")
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

    var decoded: String =""

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun sender(): String {
        post("","","") { result ->
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



        fun post(event: String,text:String,atribute: String, onResult: (String) -> Unit) {
            ser.get(
                route = "get_all_recipes",
                //params = mapOf("username" to "NakoLox", "password" to "Neko12")
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
    val gson = Gson()
    val type = object : TypeToken<ServerResponse>() {}.type
    val serverResponse: ServerResponse = gson.fromJson(response, type)

    return serverResponse.data.map { p ->
        MainActivity.Product(
            name = p.title.ifBlank { "Без названия" },
            description = p.description.ifBlank { "Тут должно быть описание" },
            imageBase64 = null, // сервер пока не присылает
            recipe = p.description_of_cooking_process.ifBlank { "Рецепта нет!" },
            rating = p.rating.toString(),
            calories = p.caloric_content.toString(),
            avtor = p.author.username.ifBlank { "Инкогнито" },
            products = if (p.ingredients.isEmpty()) {
                "Нет продуктов"
            } else {
                p.ingredients.joinToString(", ") { it.title }
            }
        )
    }
}





class MainActivity : AppCompatActivity() {

    data class Product(
        val name: String = "Без названия",
        val description: String = "Тут должно быть описание",
        val imageBase64: String? = null,
        val recipe: String = "Рецепта нет!",
        val rating: String = "",
        val calories: String = "Не указано",
        val avtor: String = "Инкогнито",
        val products: String = "Нет продуктов"
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
        val py = Py()

        val buttonSet: Button = findViewById(R.id.buttonSet)

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val imageView3: ImageView = findViewById(R.id.imageView3)
        val imageView5: ImageView = findViewById(R.id.imageView5)



        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        fun onResume() {
            super.onResume()


            adapter.recreateList(recyclerView)
        }


        adapter = ProductAdapter(this,mutableListOf())
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
                        .withEndAction {
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
                        .start()
                }
                .start()

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
                        .withEndAction {
                            val favManager = FavoritesManager(this)
                            val saved = favManager.getFavorites()

                            if (saved.toString() != "[]"){
                                val productList = saved.map {
                                    Product(it.name, it.description, it.imageBase64, it.recipe,it.rating,it.calories,it.avtor,it.products)
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
                        .start()
                }
                .start()

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
                        .withEndAction {

                            Toast.makeText(this, "11", Toast.LENGTH_SHORT).show()
                            val api=Api()
                            api.post("","","") { result ->
                                if (result.isNotBlank()) {
                                    try {
                                        println(result)
                                        val products = parseServerResponse(result)
                                        val decodeds = result.replace("\\u", "\\u")
                                            .let { Pattern.compile("\\\\u([0-9A-Fa-f]{4})").matcher(it) }
                                            .replaceAll { matchResult ->
                                                Integer.parseInt(matchResult.group(1), 16).toChar().toString()
                                            }
                                        val i =

                                        //Toast.makeText(this, "!@"+result, Toast.LENGTH_SHORT).show()

                                        println(result)

                                        adapter.updateProducts(products)



                                    } catch (e: Exception) {
                                        Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                        println(e)
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
                        .withEndAction {
                            val intent = Intent(this, Logining::class.java)
                            startActivity(intent)
                        }
                        .start()
                }
                .start()

        }


        val imageView4: ImageView = findViewById(R.id.imageView4)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val nav = findViewById<NavigationView>(R.id.navigationView)
        val item1 = nav.menu.findItem(R.id.switch1)
        val switch1 = item1.actionView?.findViewById<SwitchCompat>(R.id.switchControl)
        val item2 = nav.menu.findItem(R.id.switch2)
        val switch2 = item2.actionView?.findViewById<SwitchCompat>(R.id.switchControl)

        val stng = Settings(this)
        stng.load() // загружаем настройки из памяти

        buttonSet.setOnClickListener {
            imageView4.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    imageView4.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .start()
                }
                .start()

            drawer.openDrawer(GravityCompat.START)
        }

// Устанавливаем переключатели после того, как меню построено
        nav.post {
            val switch1 = nav.menu.findItem(R.id.switch1)
                .actionView?.findViewById<SwitchCompat>(R.id.switchControl)
            val switch2 = nav.menu.findItem(R.id.switch2)
                .actionView?.findViewById<SwitchCompat>(R.id.switchControl)
            val switch3 = nav.menu.findItem(R.id.switch3)
                .actionView?.findViewById<SwitchCompat>(R.id.switchControl)

            // Ставим состояние из настроек
            switch1?.isChecked = stng.seeKalories
            switch2?.isChecked = stng.seeReting
            switch3?.isChecked = stng.BigLook




            // Обработчик изменения
            switch1?.setOnCheckedChangeListener { _, isChecked ->
                stng.seeKalories = isChecked
                stng.save()
                onResume()
                //Toast.makeText(this, "Переключатель 1: $isChecked", Toast.LENGTH_SHORT).show()
            }

            switch2?.setOnCheckedChangeListener { _, isChecked ->
                stng.seeReting = isChecked
                stng.save()
                onResume()
            }

            switch3?.setOnCheckedChangeListener { _, isChecked ->
                stng.BigLook = isChecked
                stng.save()
                onResume()
            }
        }






    }


}

