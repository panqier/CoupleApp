package com.example.couple.data.data

import android.content.Context

data class UserProfile(
    val userId: String,
    val email: String
)

    fun saveUser(context: Context, user: UserProfile) {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("user_id", user.userId)
        editor.putString("user_email", user.email)
        editor.apply()
    }

    fun getUser(context: Context): UserProfile? {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val id = prefs.getString("user_id", null)
        val email = prefs.getString("user_email", null)
        if (email != null && id != null) {
            return UserProfile(id, email)
        }
        return null
    }

