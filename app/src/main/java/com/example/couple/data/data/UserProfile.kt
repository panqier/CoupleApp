package com.example.couple.data.data

import android.content.Context

data class UserProfile(
    val userId: String,
    val email: String,
    val userName: String,
    val imageUrl: String,
    val inviteCode: String,
    val paired: Boolean
)

    fun saveUser(context: Context, user: UserProfile) {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("user_id", user.userId)
        editor.putString("user_email", user.email)
        editor.putString("user_name", user.userName)
        editor.putString("image_url", user.imageUrl)
        editor.putString("invite_code", user.inviteCode)
        editor.putBoolean("paired", user.paired)
        editor.apply()
    }

    fun getUser(context: Context): UserProfile? {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val id = prefs.getString("user_id", null)
        val email = prefs.getString("user_email", null)
        val userName = prefs.getString("user_name", null)
        val image = prefs.getString("image_url", null)
        val inviteCode = prefs.getString("invite_code", null)
        val paired = prefs.getBoolean("paired", false)
        if (email != null && id != null && userName != null && image != null && inviteCode != null) {
            return UserProfile(id, email, userName, image, inviteCode, paired)
        }
        return null
    }

    fun clearUser(context: Context) {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }


fun saveUserName(context: Context, userName: String) {
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putString("user_name", userName)
    editor.apply()
}

fun getUserName(context: Context): String? {
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val userName = prefs.getString("user_name", null)
    if(userName != null) {
        return userName
    }
    return null
}

