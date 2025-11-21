package com.example.postopoche

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class FavoritesManager(context: Context) {

    private val prefs = context.getSharedPreferences("favorites_data", Context.MODE_PRIVATE)

    // ➤ Добавить новый элемент в избранное
    fun addFavorite(name: String, description: String, imageBase64: String) {
        val favorites = getFavoritesArray()

        // Проверяем, чтобы не было дублей
        if (!favorites.any { it.getString("name") == name }) {
            val item = JSONObject()
            item.put("name", name)
            item.put("description", description)
            item.put("imageBase64", imageBase64)

            favorites.add(item)
            saveFavoritesArray(favorites)
        }
    }

    // ➤ Удалить из избранного
    fun removeFavorite(name: String) {
        val favorites = getFavoritesArray()
        val newList = favorites.filterNot { it.getString("name") == name }
        saveFavoritesArray(newList)
    }

    // ➤ Проверить: товар уже в избранном?
    fun isFavorite(name: String): Boolean {
        val favorites = getFavoritesArray()
        return favorites.any { it.getString("name") == name }
    }

    // ➤ Получить список избранного как объекты
    fun getFavorites(): List<FavoriteItem> {
        val favorites = getFavoritesArray()
        return favorites.map {
            FavoriteItem(
                it.getString("name"),
                it.getString("description"),
                it.getString("imageBase64")
            )
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
        val imageBase64: String
    )
}
