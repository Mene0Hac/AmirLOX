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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.example.android01.Api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern


class savet(){
    var username = ""
    var token = ""
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




class LocalData (private val context: Context){

    fun load(context: Context):String{
        val settings = Settings(context)
        settings.load()
        println("!!set "+settings.temp)
        return settings.temp
    }

    val localProduct = listOf(
        Product("Салат Цезарь", "Классический салат с курицей и соусом Цезарь.", (drawableToBase64(context,R.drawable.cezar)), "Обжарить курицу, смешать с салатом, сухариками и соусом.", "4.8", "210", "admin", "1", "курица, салат, сухари"),
        Product("Борщ", "Традиционный суп из свёклы с насыщенным вкусом.", (drawableToBase64(context,R.drawable.borsh)), "Сварить бульон, добавить овощи, свёклу, варить 40 минут.", "4.9", "120", "admin", "2", "свекла, капуста, картофель"),
        Product("Паста Карбонара", "Сливочная паста с беконом и сыром.", (drawableToBase64(context,R.drawable.pasta_carbanara)), "Отварить пасту, обжарить бекон, смешать с яйцом и сыром.", "4.7", "320", "admin", "3", "паста, бекон, сыр"),
        Product("Оливье", "Классический салат с колбасой и овощами.", (drawableToBase64(context,R.drawable.olive)), "Нарезать ингредиенты и смешать с майонезом.", "4.6", "250", "admin", "4", "картофель, морковь, колбаса"),
        Product("Пицца Маргарита", "Тонкая пицца с сыром и томатами.", (drawableToBase64(context,R.drawable.pizza_margarita)), "На тесто нанести соус, сыр, томаты, запечь.", "4.8", "270", "admin", "5", "сыр, томаты, тесто"),
        Product("Греческий салат", "Свежий салат из овощей и феты.", (drawableToBase64(context,R.drawable.salat_rim)), "Нарезать овощи, добавить фету и масло.", "4.7", "150", "admin", "6", "огурец, фета, помидоры"),
        Product("Плов", "Рис с мясом и специями по восточному рецепту.", (drawableToBase64(context,R.drawable.plov)), "Обжарить мясо, добавить рис и специи, тушить.", "4.9", "330", "admin", "7", "рис, мясо, морковь"),
        Product("Шашлык", "Маринованное мясо, жаренное на углях.", (drawableToBase64(context,R.drawable.yak)), "Замариновать мясо и пожарить на мангале.", "4.8", "295", "admin", "8", "свинина, специи"),
        Product("Блины", "Тонкие румяные блины.", (drawableToBase64(context,R.drawable.blini)), "Смешать тесто и обжарить на сковороде.", "4.9", "190", "admin", "9", "мука, яйца, молоко"),
        Product("Суп-пюре", "Кремовый суп из овощей.", (drawableToBase64(context,R.drawable.sup_pure)), "Отварить овощи и измельчить блендером.", "4.5", "130", "admin", "10", "морковь, картофель, сливки"),
        Product("Курица Терияки", "Курица в сладком соусе.", (drawableToBase64(context,R.drawable.teriak_chiken)), "Обжарить курицу и залить соусом терияки.", "4.8", "240", "admin", "11", "курица, соус терияки"),
        Product("Фахитас", "Мексиканское блюдо с курицей и перцем.", (drawableToBase64(context,R.drawable.fahitas)), "Обжарить курицу и овощи, завернуть в лепешку.", "4.6", "280", "admin", "12", "курица, перец, лаваш"),
        Product("Жареный рис", "Рис с овощами по азиатски.", (drawableToBase64(context,R.drawable.ris)), "Обжарить рис с овощами и яйцом.", "4.7", "210", "admin", "13", "рис, яйцо, овощи"),
        Product("Тарталетки", "Лёгкая закуска с начинкой.", (drawableToBase64(context,R.drawable.tartaletcy)), "Наполнить корзинки салатом или паштетом.", "4.6", "110", "admin", "14", "корзинки, начинка"),
        Product("Котлеты", "Сочные домашние котлеты.", "", "Смешать фарш, сформировать котлеты и обжарить.", "4.8", "260", "admin", "15", "фарш, лук"),
        Product("Сырники", "Нежные творожные сырники.", "", "Смешать творог с мукой, жарить на сковороде.", "4.9", "180", "admin", "16", "творог, мука"),
        Product("Омлет", "Лёгкое блюдо из яиц.", (drawableToBase64(context,R.drawable.omlet)), "Взбить яйца и обжарить на масле.", "4.5", "140", "admin", "17", "яйца, масло"),
        Product("Стейк", "Говяжий стейк средней прожарки.", (drawableToBase64(context,R.drawable.yak)), "Обжарить мясо до желаемой прожарки.", "4.9", "350", "admin", "18", "говядина"),
        Product("Суши", "Рис с рыбой в рулетах.", (drawableToBase64(context,R.drawable.sysi)), "Сформировать роллы из риса и рыбы.", "4.8", "200", "admin", "19", "рис, рыба"),
        Product("Гуляш", "Мясо в густом соусе.", (drawableToBase64(context,R.drawable.gylih)), "Тушить мясо в соусе с овощами.", "4.7", "280", "admin", "20", "говядина, овощи")
    )

    val defaultProducts = mutableListOf(
        Product("Яблоко", "Свежее красное яблоко",(drawableToBase64(context,R.drawable.yak)),avtor="mene"),
        Product("Банан", "Спелый жёлтый банан", imageBase64 = load(context)),
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

    var editLits = listOf(
        Product(
            name = "Большое яблоко",
            description = "Свежее красное яблоко, идеально для десертов и салатов.",
            imageBase64 = "",
            recipe = "Помыть яблоко, нарезать на дольки и подать с медом.",
            rating = "4.5",
            calories = "95",
            avtor = "mene",
            id = "1",
            products = "яблоко"
        ),
        Product(
            name = "Не спелый банан",
            description = "Жёлтый и сладкий банан, пока слегка твердый.",
            imageBase64 = "",
            recipe = "Очистить банан, нарезать кружочками и добавить в смузи.",
            rating = "4.2",
            calories = "105",
            avtor = "mene",
            id = "2",
            products = "банан"
        )
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

fun runOnUiThread(action: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        action()
    }
}
fun drawableToBase64(context: Context, drawableId: Int): String {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!

    // Получаем Bitmap из Drawable
    val bitmap = if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bmp
    }

    // Bitmap → ByteArray
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()

    // ByteArray → Base64
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
            id = p.id.toString().ifBlank { "00" },
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
        val id: String = "00",
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
        adapter.updateProducts(LocalData(this).defaultProducts)

        var data = LocalData(this)


        button2.setOnClickListener {
            // --- Анимация ImageView ---
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
                            adapter.updateProducts(data.localProduct)
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
                                    Product(it.name, it.description, it.imageBase64, it.recipe,it.rating,it.calories,it.avtor,it.id,it.products)
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

        button1.setOnClickListener {

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

                            //Toast.makeText(this, "11", Toast.LENGTH_SHORT).show()
                            val api = Api()
                            api.get("get_all_recipes"){ result ->
                                if (result.isNotBlank()) {
                                    try {
                                        println(result)
                                        val products = parseServerResponse(result)

                                        //Toast.makeText(this, "!@"+result, Toast.LENGTH_SHORT).show()

                                        println("result="+result)
                                        println("products="+products)

                                        adapter.updateProducts(products)



                                    } catch (e: Exception) {
                                        if(e.toString()=="org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject"){
                                            Toast.makeText(this, "Не удалось подключиться к серверу", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                            println("!"+e)
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


        //Toast.makeText(this, "est.toString()" , Toast.LENGTH_SHORT).show()
            //fav.getFavorites()
            //adapter.updateProducts(data.drinks)
            //Toast.makeText(this, " Показаны напитки", Toast.LENGTH_SHORT).show()


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
                            val set = Settings(this)
                            set.load()
                            if (set.token == "" ){
                                val intent = Intent(this, Logining::class.java)
                                startActivity(intent)
                            }else{
                                val api = Api()
                                api.get("get_my_recipes",set.token){ result ->
                                    if (result.isNotBlank()) {
                                        println("!!res "+result)
                                        try {
                                            if (result == "[]"){
                                                data.editLits=data.editLits
                                            }

                                        } catch (e: Exception) {
                                            if(e.toString()=="org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject"){
                                                //Toast.makeText(this, "Не удалось подключиться к серверу", Toast.LENGTH_SHORT).show()
                                            }else{
                                                //Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                                println("!"+e)
                                            }
                                        }
                                    }
                                    else{
                                        Toast.makeText(this, "44", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                val intent = Intent(this, UserRecepi::class.java)
                                startActivity(intent)

                                //Toast.makeText(this, set.username, Toast.LENGTH_SHORT).show()


                            }
                        }
                        .start()
                }
                .start()

        }


        val imageView4: ImageView = findViewById(R.id.imageView4)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val nav = findViewById<NavigationView>(R.id.navigationView)
        val item1 = nav.menu.findItem(R.id.switch1)
        val item2 = nav.menu.findItem(R.id.switch2)

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
            val switch4 = nav.menu.findItem(R.id.switch4)
                .actionView?.findViewById<SwitchCompat>(R.id.switchControl)

            // Ставим состояние из настроек
            switch1?.isChecked = stng.seeKalories
            switch2?.isChecked = stng.seeReting
            switch3?.isChecked = stng.BigLook
            switch4?.isChecked = stng.seeId

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

            switch4?.setOnCheckedChangeListener { _, isChecked ->
                stng.seeId = isChecked
                stng.save()
                onResume()
            }
        }






    }


}

