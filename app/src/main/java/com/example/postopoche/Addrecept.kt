package com.example.postopoche

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.example.postopoche.MainActivity.Product
import android.net.Uri
import android.text.InputType
import com.yalantis.ucrop.UCrop
import java.io.File
import android.widget.EditText
import android.widget.LinearLayout



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
            if (settings.username !=""){
                val returnData = Product(
                    id = recipeId.toString(),
                    name = edName.text.toString(),
                    description = edDescription.text.toString(),
                    imageBase64 = "",
                    recipe = edRecipe.text.toString(),
                    products = edProducts.text.toString(),  // инградиенты для амира
                )
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

        recipeId = id

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

            // 👇 добавляем в edProducts, чтобы ингредиенты были видны в поле
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

