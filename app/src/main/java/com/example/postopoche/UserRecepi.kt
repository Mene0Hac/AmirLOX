package com.example.postopoche

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRecepi: AppCompatActivity() {
    data class EditProduct(
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


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitu_user_recepts)
        val data = LocalData(this)
        val settings = Settings(this)


        val buttonExit: Button = findViewById(R.id.buttonExit)
        val buttonBack: Button = findViewById(R.id.button)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)

        recyclerView = findViewById(R.id.RecyclerVievUser)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(this, mutableListOf())
        recyclerView.adapter = adapter
        adapter.initFavorites(this)
        adapter.updateProducts(data.editLits)

        buttonExit.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Выход")
            builder.setMessage("Вы действительно хотите выйти из своей учётной записи?")

            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setPositiveButton("Выйти") { dialog, _ ->
                settings.username = ""
                settings.token = ""
                settings.save()
                dialog.dismiss()
                onBackPressedDispatcher.onBackPressed()
            }

            val dialog = builder.create()
            dialog.show()
        }

        buttonAdd.setOnClickListener{
            val intent = Intent(this, Addrecept::class.java)
            startActivity(intent)

        }

        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}