package com.example.couple.data.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Paired(
    val user1: UserProfile,
    val user2: UserProfile,
    val days: List<Days>,
    val startDate: String
)

fun saveDays(context: Context,  daysList: MutableList<Days>) {
    val gson = Gson()
    val daysListJson = gson.toJson(daysList)
    val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("days", daysListJson)
    editor.apply()
}

fun getDays(context: Context): MutableList<Days> {
    val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("days", null)

    if (json != null) {
        val gson = Gson()
        val type = object : TypeToken<MutableList<Days>>() {}.type
        return gson.fromJson(json, type)
    }

    // Return an empty list if no data is found in SharedPreferences
    return mutableListOf()
}

fun saveStartDate(context: Context, startDate: String) {
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putString("start_date", startDate)
    editor.apply()
}

fun getStartDate(context: Context):  String? {
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val startDate = prefs.getString("start_date", null)
    if(startDate != null) {
        return startDate
    }
    return null
}
