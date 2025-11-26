package com.example.postopoche

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class FavoritesManager(context: Context) {

    private val prefs = context.getSharedPreferences("favorites_data", Context.MODE_PRIVATE)

    // ➤ Добавить новый элемент в избранное
    fun addFavorite(name: String, description: String, imageBase64: String,recipe: String ,rating: String,calories: String,avtor: String,products: String) {
        val favorites = getFavoritesArray()

        // Проверяем, чтобы не было дублей по имени И описанию
        if (!favorites.any {
                it.getString("name") == name && it.getString("description") == description
            }) {
            val item = JSONObject()
            item.put("name", name)
            item.put("description", description)
            item.put("imageBase64", imageBase64)
            item.put("recipe",recipe)
            item.put("rating",rating)
            item.put("calories",calories)
            item.put("avtor",avtor)
            item.put("products",products)

            favorites.add(item)
            saveFavoritesArray(favorites)
        }
    }

    // ➤ Удалить из избранного по имени И описанию
    fun removeFavorite(name: String, description: String) {
        val favorites = getFavoritesArray()
        val newList = favorites.filterNot {
            it.getString("name") == name && it.getString("description") == description
        }
        saveFavoritesArray(newList)
    }

    // ➤ Проверить: товар уже в избранном по имени И описанию
    fun isFavorite(name: String, description: String): Boolean {
        val favorites = getFavoritesArray()
        return favorites.any {
            it.getString("name") == name && it.getString("description") == description
        }
    }

    // ➤ Получить список избранного как объекты
    fun getFavorites(): List<FavoriteItem> {
        val favorites = getFavoritesArray()
        return favorites.map {
            FavoriteItem(
                it.getString("name"),
                it.getString("description"),
                it.getString("imageBase64"),
                        it.getString("recipe"),
                it.getString("rating"),
                it.getString("calories"),
                it.getString("avtor"),
                        it.getString("products")

            )
        }
    }
    fun getFavoriteKeys(): List<String> {
        val favorites = getFavoritesArray()
        return favorites.map {
            it.getString("name") + "|" + it.getString("description")
        }
    }

    // --- внутренние методы для работы с JSON ---
    private fun getFavoritesArray(): MutableList<JSONObject> {
        val jsonString = prefs.getString("favorites", "[]") ?: "[]"
        val arr = JSONArray(jsonString)

        val list = mutableListOf<JSONObject>()
        for (i in 0 until arr.length()) {
            list.add(arr.getJSONObject(i))
        }
        return list
    }

    private fun saveFavoritesArray(list: List<JSONObject>) {
        val arr = JSONArray()
        list.forEach { arr.put(it) }

        prefs.edit().putString("favorites", arr.toString()).apply()
    }

    data class FavoriteItem(
        val name: String,
        val description: String,
        val imageBase64: String,
        val recipe: String,
        val rating: String,
        val calories: String,
        val avtor: String,
        val products: String
    )
}
