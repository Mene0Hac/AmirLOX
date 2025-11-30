package com.example.postopoche

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Base64
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.postopoche.MainActivity.Product
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton


object ImageBase64Cache {
    private val cache = mutableMapOf<String, String>()

    fun put(key: String, base64: String) {
        cache[key] = base64
    }

    fun get(key: String): String? = cache[key]
}


class ProductAdapter(
    private val context: Context,
    private val products: MutableList<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    val settings = Settings(context)


    private lateinit var favManager: FavoritesManager
    private val favoriteCache = mutableSetOf<String>()


    fun initFavorites(context: Context) {
        favManager = FavoritesManager(context)
        favoriteCache.clear()
        favoriteCache.addAll(favManager.getFavoriteKeys())
        notifyDataSetChanged()
    }

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(
        val card: CardView,
        val name: TextView,
        val description: TextView,
        val image: ImageView,
        val recipe: TextView,
        val rating: TextView,
        val calories: TextView,
        val avtor: TextView,
        val id: TextView,
        val products: TextView,
        val favoriteButton: MaterialButton
    ) : RecyclerView.ViewHolder(card) {

        fun setProductImage(base64: String?) {
            if (base64.isNullOrBlank()) {
                image.setImageResource(R.drawable.apple)
                return
            }

            try {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.setImageBitmap(bmp)
            } catch (e: Exception) {
                image.setImageResource(R.drawable.apple)
            }
        }

    }

    fun recreateList(recyclerView: RecyclerView) {
        settings.load()
        recyclerView.recycledViewPool.clear()
        recyclerView.adapter = null
        recyclerView.adapter = this
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val context = parent.context

        var sizecards: Double = 0.6
        var rad = 0.8f
        var imgs = 1.0
        var texts = 1.0f
        var iconS = 0

        settings.load()
        if (settings.BigLook == true) {
            sizecards = 1.0
            rad = 1f
            imgs = 1.0
            texts = 1.0f
            iconS = 0
        } else {
            sizecards = 0.6
            rad = 0.8f
            imgs = 0.8
            texts = 0.9f
            iconS = -5
        }

        if (!::favManager.isInitialized) {
            initFavorites(context)
        }

        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels

        val imageSize = (screenWidth * 0.275 * imgs).toInt()
        val margin = (screenWidth * 0.035 * sizecards).toInt()
        val padding = (screenWidth * 0.04 * sizecards).toInt()
        val radius = screenWidth * 0.06f * rad

        val card = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(margin, margin, margin, margin) }

            this.radius = radius
            cardElevation = radius / 2
        }

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.parseColor("#FF9933"), Color.parseColor("#FFD580"))
        ).apply { cornerRadius = radius }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = gradient
            setPadding(padding, padding, padding, padding)
        }

        val image = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply { cornerRadius = radius }
            clipToOutline = true
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((padding * 0.7 * sizecards).toInt(), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, rad * 1f)
        }

        val id = TextView(context).apply {
            textSize = rad * 13f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (padding * sizecards * 0.2).toInt()
            }
        }

        val nameRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val name = TextView(context).apply {
            textSize = 20f * texts
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = (padding * sizecards * 0.2).toInt()
            }
        }

        val favoriteButton = MaterialButton(context).apply {
            icon = null
            iconSize = 50 + iconS
            iconTint = ColorStateList.valueOf(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                120,
                LinearLayout.LayoutParams.WRAP_CONTENT,

            )
        }

        nameRow.addView(name)
        nameRow.addView(favoriteButton)

        // Описание
        val description = TextView(context).apply {
            textSize = texts * 13f
            setTextColor(Color.parseColor("#FFF8E7"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (padding * sizecards * 0.2).toInt()
                topMargin = (padding * sizecards * 0.1).toInt()
                bottomMargin = (padding * sizecards * 0.4).toInt()
            }
        }

        val rating = TextView(context).apply {
            textSize = texts * 13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }
        val calories = TextView(context).apply {
            textSize = texts * 13f
            setTextColor(Color.parseColor("#FFF8E7"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginStart = (padding * sizecards * 0.2).toInt() }
        }

        val ratingCaloriesLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }


        ratingCaloriesLayout.addView(calories)
        ratingCaloriesLayout.addView(rating)

        val recipe = TextView(context)
        val avtor = TextView(context)
        val products = TextView(context)


        textLayout.addView(nameRow)
        textLayout.addView(description)
        textLayout.addView(ratingCaloriesLayout)

        root.addView(image)
        root.addView(textLayout)

        // Новый контейнер
        val container = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val idParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP or Gravity.END
        ).apply {
            topMargin = 14
            rightMargin = 20
        }

        id.layoutParams = idParams
        id.textSize = 12f  // маленький размер

        container.addView(root)  // главная разметка
        container.addView(id)    // ID поверх

        card.addView(container)


        return ProductViewHolder(card, name, description, image, recipe, rating, calories, avtor, id, products, favoriteButton)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = products[position]

        val key = "img_$position"   // уникальный ключ

        val realBase64 = ImageBase64Cache.get(key) ?: product.imageBase64

        if (product.imageBase64 != null && ImageBase64Cache.get(key) == null) {
            ImageBase64Cache.put(key, product.imageBase64!!)
        }
        // Пытаемся взять из кеша
        val cached = ImageBase64Cache.get(key)

        val imageBase64 = cached ?: product.imageBase64

        // Если нет в кеше — добавляем туда
        if (cached == null && !imageBase64.isNullOrEmpty()) {
            ImageBase64Cache.put(key, imageBase64)
        }

        holder.setProductImage(imageBase64)




        fun AddStar(str: String): String{
            if (str==""){
                return "Нет оценок"
            }
            else{
                return ("★"+str)
            }
        }

        settings.load()

        if ((product.name.length>=12) and (settings.BigLook)){
            holder.name.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }else if ((product.name.length>=18) and !(settings.BigLook)){
            holder.name.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        holder.name.text = product.name
        holder.description.text = product.description
        holder.setProductImage(product.imageBase64)
        holder.recipe.text = product.recipe


        // Показывать или скрывать рейтинг
        holder.rating.visibility =
            if (settings.seeReting == true) View.VISIBLE else View.GONE

        holder.calories.visibility =
            if (settings.seeKalories == true) View.VISIBLE else View.GONE

        holder.id.visibility =
            if (settings.seeId == true) View.VISIBLE else View.GONE

        holder.rating.text = AddStar(product.rating)
        holder.calories.text = product.calories
        holder.avtor.text = product.avtor
        holder.id.text = product.id
        holder.products.text = product.products


        if (settings.username != product.avtor){
            holder.favoriteButton.icon = if (key in favoriteCache) ContextCompat.getDrawable(context, R.drawable.staron) else ContextCompat.getDrawable(context, R.drawable.staroff)
        }else{
            holder.favoriteButton.icon = ContextCompat.getDrawable(context, R.drawable.edit)

        }

        holder.favoriteButton.setOnClickListener {
            if (settings.username!=holder.avtor.text)
                if (key in favoriteCache) {
                    favoriteCache.remove(key)
                    favManager.removeFavorite(product.name, product.description)
                    holder.favoriteButton.icon = ContextCompat.getDrawable(context, R.drawable.staroff)
                    Toast.makeText(holder.card.context, "Удалено", Toast.LENGTH_SHORT).show()

                } else {
                    favoriteCache.add(key)
                    favManager.addFavorite(product.name, product.description, product.imageBase64 ?: "", product.recipe ?: "", product.rating ?: "", product.calories ?: "", product.avtor ?: "",product.id ?: "",product.products ?: "")
                    holder.favoriteButton.icon = ContextCompat.getDrawable(context, R.drawable.staron)
                    Toast.makeText(holder.card.context, "Добавлено", Toast.LENGTH_SHORT).show()
                }
            else {
                val key = product.id ?: product.name
                val realBase64 = ImageBase64Cache.get(key) ?: product.imageBase64

                val intent = Intent(holder.card.context, Addrecept::class.java)
                intent.putExtra("name", product.name)
                intent.putExtra("description", product.description)
                intent.putExtra("imageBase64", realBase64)
                intent.putExtra("recipe", product.recipe)
                intent.putExtra("rating", product.rating)
                intent.putExtra("calories", product.calories)
                intent.putExtra("avtor", product.avtor)
                intent.putExtra("id", product.id)
                intent.putExtra("products", product.products)

                holder.card.context.startActivity(intent)
            }

        }

        holder.card.setOnClickListener {
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction {

                    val key = product.id ?: product.name
                    val realBase64 = ImageBase64Cache.get(key) ?: product.imageBase64

                    val intent = Intent(holder.card.context, ProductDetails::class.java)
                    intent.putExtra("name", product.name)
                    intent.putExtra("description", product.description)
                    intent.putExtra("imageBase64", realBase64)
                    intent.putExtra("recipe", product.recipe)
                    intent.putExtra("rating", product.rating)
                    intent.putExtra("calories", product.calories)
                    intent.putExtra("avtor", product.avtor)
                    intent.putExtra("id", product.id)
                    intent.putExtra("products", product.products)

                    holder.card.context.startActivity(intent)
                }.start()
            }.start()
        }

    }

    override fun getItemCount() = products.size
}
