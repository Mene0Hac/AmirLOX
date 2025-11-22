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

class ProductAdapter(
    private val products: MutableList<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

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
        val image: ImageView,
        val name: TextView,
        val description: TextView,
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val context = parent.context

        if (!::favManager.isInitialized) {
            initFavorites(context)   // загружаем избранное один раз
        }

        // --- дальше идёт твой код создания UI ---
        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels

        val imageSize = (screenWidth * 0.20).toInt()
        val margin = (screenWidth * 0.035).toInt()
        val padding = (screenWidth * 0.04).toInt()
        val radius = screenWidth * 0.06f

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
            layoutParams = LinearLayout.LayoutParams(imageSize, imageSize)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply { cornerRadius = radius }
            clipToOutline = true
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((padding * 0.7).toInt(), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val nameLayout = LinearLayout(context)

        val name = TextView(context).apply {
            textSize = 18f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val favoriteButton = Button(context).apply {
            text = "☆"
            textSize = 28f
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
        }

        val description = TextView(context).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#FFF8E7"))
        }

        nameLayout.addView(name)
        nameLayout.addView(favoriteButton)
        textLayout.addView(nameLayout)
        textLayout.addView(description)

        root.addView(image)
        root.addView(textLayout)
        card.addView(root)

        return ProductViewHolder(card, image, name, description, favoriteButton)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val key = "${product.name}|${product.description}"

        holder.name.text = product.name
        holder.description.text = product.description
        holder.setProductImage(product.imageBase64)

        // --- быстрый O(1) доступ ---
        holder.favoriteButton.text = if (key in favoriteCache) "★" else "☆"

        holder.favoriteButton.setOnClickListener {
            if (key in favoriteCache) {
                favoriteCache.remove(key)
                favManager.removeFavorite(product.name, product.description)
                holder.favoriteButton.text = "☆"
                Toast.makeText(holder.card.context, "Удалено", Toast.LENGTH_SHORT).show()

            } else {
                favoriteCache.add(key)
                favManager.addFavorite(product.name, product.description, product.imageBase64 ?: "")
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
                    holder.card.context.startActivity(intent)
                }.start()
            }.start()
        }
    }

    override fun getItemCount() = products.size
}
