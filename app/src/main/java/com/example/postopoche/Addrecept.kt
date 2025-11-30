package com.example.postopoche

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.postopoche.MainActivity.Product
import java.io.ByteArrayOutputStream
import android.net.Uri
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.InputStream




class Addrecept : AppCompatActivity() {
    private lateinit var edName: EditText
    private lateinit var edDescription: EditText
    private lateinit var edRecipe: EditText
    private lateinit var edProducts: EditText
    private lateinit var edCalories: EditText
    private lateinit var imgRecipe: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnChooseImage: Button

    private var currentImageBase64: String? = null
    private var recipeId: String? = null

    var img:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = Settings(this)
        setContentView(R.layout.activity_add_recept)

        edName = findViewById(R.id.edName)
        edDescription = findViewById(R.id.edDescription)
        edRecipe = findViewById(R.id.edRecipe)

        edProducts = findViewById(R.id.edProducts)
        edCalories = findViewById(R.id.edCalories)
        imgRecipe = findViewById(R.id.imgRecipe)

        btnSave = findViewById(R.id.btnSave)
        btnChooseImage = findViewById(R.id.btnChooseImage)

        loadIncomingData()

        btnSave.setOnClickListener {
            settings.load()
            println("111img "+img)
            settings.temp = img
            settings.save()
            if (settings.username !=""){
                val returnData = Pair("token=${settings.token}", Product(
                    name = edName.text.toString(),
                    description = edDescription.text.toString(),
                    imageBase64 = "",
                    recipe = edRecipe.text.toString(),
                    rating = "",
                    calories = edCalories.text.toString(),
                    avtor = "",
                    id = recipeId.toString(),
                    products = edProducts.text.toString()
                ))
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

        recipeId = id

        Toast.makeText(this, "!"+id, Toast.LENGTH_SHORT).show()
        // если данные пришли — это режим редактирования
        if (name != null) {
            edName.setText(name)
            edDescription.setText(desc)
            edRecipe.setText(recipe)
            edProducts.setText(products)
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
        }else imgRecipe.setImageResource(R.drawable.apple)
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
