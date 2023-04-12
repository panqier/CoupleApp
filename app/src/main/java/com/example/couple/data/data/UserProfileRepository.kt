package com.example.couple.data.data

import android.app.Application
import android.content.Context

class UserProfileRepository constructor(val mContext: Context){

    companion object {
        private var INSTANCE: UserProfileRepository? = null

        fun getInstance(application: Application): UserProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserProfileRepository(application).also { INSTANCE = it }
            }
        }

    }

}