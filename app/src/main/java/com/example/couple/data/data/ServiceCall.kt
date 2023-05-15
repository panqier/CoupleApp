package com.example.couple.data.data

import android.app.Application
import com.example.couple.data.data.UserProfileRepository.Companion.retrieveCurrentDays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceCall : Application(){
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Main).launch {
            retrieveCurrentDays(this@ServiceCall).await()
        }
    }
}