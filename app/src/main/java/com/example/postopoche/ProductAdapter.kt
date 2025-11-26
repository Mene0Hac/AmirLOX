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
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout


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
        val products: TextView,
        val favoriteButton: Button
    ) : RecyclerView.ViewHolder(card) {

        fun setProductImage(base64: String?) {
            if (!base64.isNullOrBlank()) {
                try {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bmp != null) {
                        image.setImageBitmap(bmp)
                        return
                    }
                } catch (_: Exception) {}
            }
            image.setImageResource(R.drawable.apple)
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

        var r: Double =0.6
        var t= 0.8f
        var imgs = 1.0
        var texts = 1.0f

        settings.load()
        if (settings.BigLook == true){
            r = 1.0
            t = 1f
            imgs = 1.0
            texts = 1.0f
        }else{
            r = 0.6
            t = 0.8f
            imgs = 0.8
            texts = 0.9f
        }


        if (!::favManager.isInitialized) {
            initFavorites(context)   // загружаем избранное один раз
        }

        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels

        val imageSize = (screenWidth * 0.275*imgs).toInt()
        val margin = (screenWidth * 0.035 *r).toInt()
        val padding = (screenWidth * 0.04 *r).toInt()
        val radius = screenWidth * 0.06f * t

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
            layoutParams = LinearLayout.LayoutParams(
                imageSize,
                imageSize
            ).apply {
                // Картинка остаётся слева и НЕ растягивается
                gravity = Gravity.CENTER_VERTICAL

            }

            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply { cornerRadius = radius }
            clipToOutline = true
        }


        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((padding * 0.7*r).toInt(), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, t*1f)
        }

        val nameLayout = LinearLayout(context)

        val name = TextView(context).apply {
            textSize = 20f *texts
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, t*1f).apply {
                marginStart = (padding * r*0.2).toInt()
            }

        }

        val favoriteButton = Button(context).apply {
            text = "☆"
            textSize = 28f * texts
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.END
            )
        }

        val description = TextView(context).apply {
            textSize = texts*13f
            setTextColor(Color.parseColor("#FFF8E7"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (padding * r*0.2).toInt()
                topMargin = (padding * r*0.1).toInt()   //  ОТСТУП СВЕРХУ
                bottomMargin = (padding * r*0.4).toInt() //  ОТСТУП СНИЗУ
            }
        }
        description.setPadding(0, (padding * 0.3*r).toInt(), 0, (padding * 0.4*r).toInt())



        val rating = TextView(context).apply {
            textSize = texts*13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }
        val calories = TextView(context).apply {
            textSize = texts*13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }


        val recipe = TextView(context).apply {
            textSize = t*13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }

        val avtor = TextView(context).apply {
            textSize = t*13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }

        val products = TextView(context).apply {
            textSize = t*13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }



        nameLayout.addView(name)
        nameLayout.addView(favoriteButton)
        textLayout.addView(nameLayout)
        textLayout.addView(description)

        val ratingCaloriesLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        calories.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f*t
        ).apply {
            marginStart = (padding * r*0.2).toInt()
        }

        rating.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        ratingCaloriesLayout.addView(calories)
        ratingCaloriesLayout.addView(rating)


        textLayout.addView(ratingCaloriesLayout)


        root.addView(image)
        root.addView(textLayout)
        card.addView(root)

        return ProductViewHolder(card, name, description, image , recipe,rating,calories, avtor,products, favoriteButton)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val key = "${product.name}|${product.description}"

        fun AddStar(str: String): String{
            if (str==""){
                return "Нет оценок"
            }
            else{
                return ("★"+str)
            }
        }


        holder.name.text = product.name
        holder.description.text = product.description
        holder.setProductImage(product.imageBase64)
        holder.recipe.text = product.recipe


        settings.load()


        // Показывать или скрывать рейтинг
        holder.rating.visibility =
            if (settings.seeReting == true) View.VISIBLE else View.GONE

        holder.calories.visibility =
            if (settings.seeKalories == true) View.VISIBLE else View.GONE

        holder.rating.text = AddStar(product.rating)
        holder.calories.text = product.calories
        holder.avtor.text = product.avtor
        holder.products.text = product.products

        holder.favoriteButton.text = if (key in favoriteCache) "★" else "☆"

        holder.favoriteButton.setOnClickListener {
            if (key in favoriteCache) {
                favoriteCache.remove(key)
                favManager.removeFavorite(product.name, product.description)
                holder.favoriteButton.text = "☆"
                Toast.makeText(holder.card.context, "Удалено", Toast.LENGTH_SHORT).show()

            } else {
                favoriteCache.add(key)
                favManager.addFavorite(product.name, product.description, product.imageBase64 ?: "", product.recipe ?: "", product.rating ?: "", product.calories ?: "", product.avtor ?: "",product.products ?: "")
                holder.favoriteButton.text = "★"
                Toast.makeText(holder.card.context, "Добавлено", Toast.LENGTH_SHORT).show()
            }
        }

        holder.card.setOnClickListener {
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction {
                    val intent = Intent(holder.card.context, ProductDetails::class.java)
                    intent.putExtra("name", product.name)
                    intent.putExtra("description", product.description)
                    intent.putExtra("imageBase64", product.imageBase64)
                    intent.putExtra("recipe", product.recipe)
                    intent.putExtra("rating", product.rating)
                    intent.putExtra("calories", product.calories)
                    intent.putExtra("avtor", product.avtor)
                    intent.putExtra("products", product.products)
                    holder.card.context.startActivity(intent)
                }.start()
            }.start()
        }
    }

    override fun getItemCount() = products.size
}
