package com.example.postopoche

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.net.Uri
import android.text.InputType
import com.yalantis.ucrop.UCrop
import java.io.File
import android.widget.EditText
import android.widget.LinearLayout
import com.example.android01.Api
import com.google.gson.Gson


class Addrecept : AppCompatActivity() {
    private lateinit var edName: EditText
    private lateinit var edDescription: EditText
    private lateinit var edRecipe: EditText
    private lateinit var edProducts: EditText
    private lateinit var btvAdd: Button
    private lateinit var btvDel: Button
    private lateinit var edCalories: EditText
    private lateinit var imgRecipe: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnChooseImage: Button

    private var currentImageBase64: String? = null
    private var recipeId: String? = null




    fun showAddProductDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Добавить продукт")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val productInput = EditText(this)
        productInput.hint = "Название продукта"
        layout.addView(productInput)

        val countInput = EditText(this)
        countInput.hint = "Количество"
        countInput.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(countInput)

        builder.setView(layout)

        builder.setPositiveButton("Добавить") { _, _ ->
            val product = productInput.text.toString()
            val count = countInput.text.toString().toIntOrNull() ?: 0

            products.add(product)
            counts.add(count)
        }

        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    val products: MutableList<String> = mutableListOf()
    val counts: MutableList<Int> = mutableListOf()
    val data: MutableList<List<Any>> = mutableListOf(products, counts)

    data class RecipeRequest(
        val id: String,
        val title: String,
        val description: String,
        val imageBase64: String,
        val description_of_cooking_process: String,
        val caloric_content: String,
        val ingredients: Map<String, String>
    )
    var img:String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = Settings(this)
        setContentView(R.layout.activity_add_recept)

        edName = findViewById(R.id.edName)
        edDescription = findViewById(R.id.edDescription)
        edRecipe = findViewById(R.id.edRecipe)

        btvAdd = findViewById(R.id.buttonAddProduct)
        btvDel = findViewById(R.id.buttonDelProduct)

        edProducts = findViewById(R.id.edProducts)
        edCalories = findViewById(R.id.edCalories)
        imgRecipe = findViewById(R.id.imgRecipe)

        edProducts = findViewById(R.id.edProducts)

        btnSave = findViewById(R.id.btnSave)
        btnChooseImage = findViewById(R.id.btnChooseImage)

        loadIncomingData()

        btvAdd.setOnClickListener {
            AddProduct()
        }
        btvDel.setOnClickListener {
            DelProduct()
        }

        btnSave.setOnClickListener {
            settings.load()
            val token = settings.token
            println("!!daTa= "+data)
            settings.temp = img
            settings.save()
            var id = recipeId.toString()
            val gson = Gson()
            fun listsToMap(keys: List<String>, values: MutableList<Int>): Map<String, String> {
                require(keys.size == values.size) { "Разный размер списков!!" }           // Проверяем, что списки одинаковой длины
                val array2d = keys.indices.map { index -> listOf(keys[index], values[index]) }
                return array2d.associate { (it[0] to it[1].toString()) as Pair<String, String> }
            }

            val returndata = gson.toJson(
                mapOf(
                    "id" to recipeId.toString(),
                    "title" to edName.text.toString(),
                    "description" to edDescription.text.toString(),
                    "imageBase64" to "",
                    "description_of_cooking_process" to edRecipe.text.toString(),
                    "caloric_content" to edCalories.text.toString(),
                    "ingredients" to listsToMap(products, counts)   // ← ВАЖНО!
                )
            )

            if (id!="") {
                val api = Api()
                api.post("/create_recipe", token,returndata) { result ->
                    if (result.isNotBlank()) {
                        println("!!res " + result)
                        try {

                        } catch (e: Exception) {
                            if (e.toString() == "org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject") {
                                //Toast.makeText(this, "Не удалось подключиться к серверу", Toast.LENGTH_SHORT).show()
                            } else {
                                //Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                println("!" + e)
                            }
                        }
                    }
                }
            }else{
                val api = Api()
                api.post("/test", token,returndata) { result ->
                    if (result.isNotBlank()) {
                        println("!!res1 " + result)
                        try {

                        } catch (e: Exception) {
                            if (e.toString() == "org.json.JSONException: Value Ошибка of type java.lang.String cannot be converted to JSONObject") {
                                //Toast.makeText(this, "Не удалось подключиться к серверу", Toast.LENGTH_SHORT).show()
                            } else {
                                //Toast.makeText(this, "!"+e, Toast.LENGTH_SHORT).show()
                                println("!" + e)
                            }
                        }
                    }
                }

            }
        }
        chooseImage()
    }

    private fun loadIncomingData() {
        val i = intent
        val id = i.getStringExtra("id")
        val name = i.getStringExtra("name")
        val desc = i.getStringExtra("description")
        val image64 = i.getStringExtra("imageBase64")
        val recipe = i.getStringExtra("recipe")
        val calories = i.getStringExtra("calories")
        val products = i.getStringExtra("products")
        //val products = i.getStringExtra("products")

        //val products: String = intent.getStringExtra("products") ?: ""

        addIngredientsFromString(products)   // !!
        recipeId = id


        println("ppp"+products)

        Toast.makeText(this, "!"+id, Toast.LENGTH_SHORT).show()
        // если данные пришли — это режим редактирования
        if (name != null) {
            edName.setText(name)
            edDescription.setText(desc)
            edRecipe.setText(recipe)
            edProducts.setText(data.toString())
            edCalories.setText(calories)

            currentImageBase64 = image64
            if ((image64 != "")and(image64!=null)) {
                val bytes = Base64.decode(image64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgRecipe.setImageBitmap(bitmap)
            }else{
                imgRecipe.setImageResource(R.drawable.apple)
            }
            btnSave.text = "Сохранить изменения"
        }
    }

    fun AddProduct() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Добавить продукт")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val productInput = EditText(this)
        productInput.hint = "Название продукта"
        layout.addView(productInput)

        val countInput = EditText(this)
        countInput.hint = "Количество"
        countInput.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(countInput)

        builder.setView(layout)

        builder.setPositiveButton("Добавить") { _, _ ->
            val product = productInput.text.toString()
            val count = countInput.text.toString().toIntOrNull() ?: 0

            products.add(product)
            counts.add(count)

            // добавляем в edProducts, чтобы ингредиенты были видны в поле
            edProducts.setText(products.joinToString(", ") + "\n" + counts.joinToString(", "))
        }

        builder.setNegativeButton("Отмена", null)
        builder.show()

    }

    fun DelProduct() {
        if (products.isEmpty()) {
            Toast.makeText(this, "Список продуктов пуст", Toast.LENGTH_SHORT).show()
            return
        }

        // Массив продуктов для отображения в диалоге
        val items = products.toTypedArray()

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Удалить продукт")

        builder.setItems(items) { _, which ->
            // which — индекс выбранного элемента
            val deletedProduct = products[which]
            val deletedCount = counts[which]

            products.removeAt(which)
            counts.removeAt(which)

            Toast.makeText(this, "Удалено: $deletedProduct ($deletedCount)", Toast.LENGTH_SHORT).show()

            // обновляем поле edProducts
            edProducts.setText(
                products.joinToString(", ") + "\n" + counts.joinToString(", ")
            )
        }

        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    private fun chooseImage() {
        btnChooseImage.setOnClickListener {
            val pick = Intent(Intent.ACTION_GET_CONTENT)
            pick.type = "image/*"
            startActivityForResult(pick, 100)
        }
    }

    fun addIngredientsFromString(ingredientsStr: String?) {
        if (ingredientsStr.isNullOrBlank()) return

        // Разделяем по запятой, чтобы получить отдельные элементы
        val items = ingredientsStr.split(",").map { it.trim() }

        for (item in items) {
            // Разделяем по "||"
            val parts = item.split("||").map { it.trim() }
            if (parts.size == 2) {
                val product = parts[0]
                val count = parts[1].toIntOrNull() ?: 0

                // Добавляем в массивы
                products.add(product)
                counts.add(count)
            }
        }
    }








    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {

            100 -> { // пользователь выбрал изображение
                val sourceUri = data.data ?: return

                // Здесь добавляем uCrop
                val destinationUri = Uri.fromFile(File(cacheDir, "cropped.jpg"))

                UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1f, 1f)     // квадрат
                    .withMaxResultSize(800, 800) // максимальное разрешение
                    .start(this)                 // this = Activity
            }

            UCrop.REQUEST_CROP -> { // результат обрезки
                val resultUri = UCrop.getOutput(data) ?: return

                val stream = contentResolver.openInputStream(resultUri)
                val bytes = stream?.readBytes()
                stream?.close()

                if (bytes != null) {
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    img = base64
                    currentImageBase64 = base64

                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imgRecipe.setImageBitmap(bmp)
                }
            }
        }
    }



}

