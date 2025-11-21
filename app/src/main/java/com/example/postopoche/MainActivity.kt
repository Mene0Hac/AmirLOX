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
import android.view.WindowManager
import androidx.core.content.ContextCompat

import org.json.JSONArray
import org.json.JSONObject

class SavedProducts(context: Context) {

    private val prefs = context.getSharedPreferences("saved_products", Context.MODE_PRIVATE)

    fun saveProducts(products: List<MainActivity.Product>) {
        val arr = JSONArray()

        for (p in products) {
            val obj = JSONObject()
            obj.put("name", p.name)
            obj.put("description", p.description)
            obj.put("imageBase64", p.imageBase64 ?: "")
            obj.put("recipe", p.recipe)
            arr.put(obj)
        }

        prefs.edit().putString("products_json", arr.toString()).apply()

    }

    fun loadProducts(): MutableList<MainActivity.Product> {
        val jsonString = prefs.getString("products_json", null) ?: return mutableListOf()
        val arr = JSONArray(jsonString)

        val list = mutableListOf<MainActivity.Product>()

        for (i in 0 until arr.length()) {
            val p = arr.getJSONObject(i)
            list.add(
                MainActivity.Product(
                    name = p.getString("name"),
                    description = p.getString("description"),
                    imageBase64 = p.getString("imageBase64"),
                    recipe = p.getString("recipe")
                )
            )
        }

        return list
    }
}



class LocalData {

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



class ButtonHandler(
    private val context: Context,
    private val button: Button,
    private val onProductsReceived: (List<MainActivity.Product>) -> Unit
) {
    init {
        button.setOnClickListener {
            val py = Py()
            py.sender { response ->
                if (response.isNotBlank()) {
                    try {
                        val products = parseServerResponse(response)
                        if (products.isNotEmpty()) {
                            onProductsReceived(products)
                            Toast.makeText(context, "писок обновлён с сервера", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Нет корректных данных", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Ошибка парсинга данных", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, " Пустой ответ от сервера", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun parseServerResponse(response: String): List<MainActivity.Product> {
        val regex = Regex("""Product\("([^"]+)",\s*"([^"]+)",\s*"([^"]*)",\s*"([^"]*)"\)""")
        return regex.findAll(response).map { match ->
            val (name, desc, base64, recipe) = match.destructured
            MainActivity.Product(name, desc, base64, recipe)
        }.toList()
    }
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

        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val data = LocalData()

        adapter = ProductAdapter(data.fruits.toMutableList())
        recyclerView.adapter = adapter

        ButtonHandler(this, button1) { newProducts ->
            adapter.updateProducts(newProducts)
        }

        // 🔹 button2 – локальный список овощей
        button2.setOnClickListener {
            adapter.updateProducts(data.vegetables)
            Toast.makeText(this, "🥕 Показаны овощи", Toast.LENGTH_SHORT).show()
        }

        // 🔹 button3 – локальный список напитков
        button3.setOnClickListener {
            adapter.updateProducts(data.drinks)
            Toast.makeText(this, "☕ Показаны напитки", Toast.LENGTH_SHORT).show()
        }
        buttonReg.setOnClickListener {
            val intent = Intent(this, Logining::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val appleBase64 = "" // вставь строку base64, если есть

        val defaultProducts = mutableListOf(
            Product("Яблоко", "Свежее красное яблоко", appleBase64),
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


        adapter = ProductAdapter(defaultProducts)
        recyclerView.adapter = adapter


        ButtonHandler(this, button1) { newProducts ->
            adapter.updateProducts(newProducts)
        }
    }

    inner class ProductAdapter(private var products: MutableList<Product>) :
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(val card: CardView) : RecyclerView.ViewHolder(card) {
            val name: TextView
            val description: TextView
            val image: ImageView
            val favoriteButton: Button

            init {
                val layout = card.getChildAt(0) as LinearLayout
                image = layout.getChildAt(0) as ImageView

                val textLayout = layout.getChildAt(1) as LinearLayout

                // Теперь textLayout.getChildAt(0) — это nameAndButtonLayout (LinearLayout)
                val nameAndButtonLayout = textLayout.getChildAt(0) as LinearLayout
                name = nameAndButtonLayout.getChildAt(0) as TextView  // Название
                favoriteButton = nameAndButtonLayout.getChildAt(1) as Button  // Кнопка

                description = textLayout.getChildAt(1) as TextView  // Описание
            }

            fun setProductImage(base64String: String?) {
                if (!base64String.isNullOrBlank()) {
                    try {
                        val bytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null) {
                            image.setImageBitmap(bitmap)
                            return
                        }
                    } catch (_: Exception) {}
                }
                image.setImageResource(R.drawable.apple)
            }
        }


        // обновление списка из сервера
        fun updateProducts(newProducts: List<Product>) {
            products.clear()
            products.addAll(newProducts)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val context = parent.context
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels

            val imageSize = (screenWidth * 0.20).toInt()
            val margin = (screenWidth * 0.035).toInt()
            val padding = (screenWidth * 0.04).toInt()
            val radius = screenWidth * 0.06f

            val card = CardView(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(margin, margin, margin, margin)
                }
                this.radius = radius
                cardElevation = radius / 2
            }

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#FF9933"), Color.parseColor("#FFD580"))
            ).apply { cornerRadius = radius }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                background = gradientDrawable
                setPadding(padding, padding, padding, padding)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val image = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(imageSize, imageSize)
                scaleType = ImageView.ScaleType.CENTER_CROP
                background = context.getDrawable(android.R.drawable.picture_frame)
                clipToOutline = true
            }

            val textLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding((padding * 0.7).toInt(), 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            // Создаём горизонтальный layout для названия и кнопки (чтобы кнопка была справа от названия)
            val nameAndButtonLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val name = TextView(context).apply {
                textSize = 18f
                setTextColor(Color.WHITE)
                setShadowLayer(2f, 1f, 1f, Color.parseColor("#AA000000"))
                layoutParams = LinearLayout.LayoutParams(
                    0,  // Ширина 0, чтобы weight работал
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f  // Название занимает всё доступное пространство слева
                )
            }

            val description = TextView(context).apply {
                textSize = 13f
                setTextColor(Color.parseColor("#FFF8E7"))
            }

            // ⭐ КНОПКА "ИЗБРАННОЕ" — круглая, справа от названия
            val favoriteButton = Button(context).apply {
                text = "❤"
                textSize = 30f

                setBackgroundColor(Color.TRANSPARENT)

                setTextColor(Color.WHITE)

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,  // Ширина по содержимому
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = 16  // Фиксированный промежуток между названием и кнопкой (в dp, можно заменить на (screenWidth * 0.02).toInt() для адаптивности)
                }
            }

            // Добавляем элементы в nameAndButtonLayout: название слева, кнопка справа
            nameAndButtonLayout.addView(name)
            nameAndButtonLayout.addView(favoriteButton)

            // Добавляем в textLayout: сначала nameAndButtonLayout, затем description
            textLayout.addView(nameAndButtonLayout)
            textLayout.addView(description)

            // Добавляем в основной layout
            layout.addView(image)
            layout.addView(textLayout)
            card.addView(layout)

            return ProductViewHolder(card)
        }


        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = products[position]
            holder.name.text = product.name
            holder.description.text = product.description
            holder.setProductImage(product.imageBase64)

            val fav = FavoritesManager(holder.card.context)

            // меняем иконку в зависимости от состояния
            if (fav.isFavorite(product.name)) {
                holder.favoriteButton.text = "★"
            } else {
                holder.favoriteButton.text = "☆"
            }

            holder.favoriteButton.setOnClickListener {
                if (fav.isFavorite(product.name)) {

                    // удалить
                    fav.removeFavorite(product.name)
                    holder.favoriteButton.text = "☆"
                    Toast.makeText(holder.card.context, "Удалено из избранного", Toast.LENGTH_SHORT).show()

                } else {

                    // сохранить
                    fav.addFavorite(product.name, product.description, product.imageBase64 ?: "")
                    holder.favoriteButton.text = "★"
                    Toast.makeText(holder.card.context, "Добавлено в избранное ⭐", Toast.LENGTH_SHORT).show()
                }
            }



            // 🔥 открытие ProductDetails
            holder.card.setOnClickListener {
                val intent = Intent(holder.card.context, ProductDetails::class.java)
                intent.putExtra("name", product.name)
                intent.putExtra("description", product.description)
                intent.putExtra("imageBase64", product.imageBase64)
                holder.card.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = products.size
    }

}
