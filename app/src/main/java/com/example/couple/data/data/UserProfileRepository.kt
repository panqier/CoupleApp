package com.example.couple.data.data

import android.app.Application
import android.content.Context
import com.example.couple.util.InviteCodeHash
import com.example.couple.util.UidDecode
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileRepository constructor(val mContext: Context){

    companion object {
        private var INSTANCE: UserProfileRepository? = null

        fun getInstance(application: Application): UserProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserProfileRepository(application).also { INSTANCE = it }
            }
        }

        @JvmStatic
        fun retrieveCurrentUser(
            db: FirebaseFirestore,
            uid: String,
            email: String,
            callback: (UserProfile) -> Unit,
            errorCallback: (Exception) -> Unit
        ) {
            val docRef = db.collection("User").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    val userProfile = if (document != null && document.exists()) {
                        val userName = document.getString("userName") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val paired = document.getBoolean("paired") ?: false
                        UserProfile(
                            userId = UidDecode.getUserNumericId(),
                            email = email,
                            userName = userName,
                            imageUrl = imageUrl,
                            inviteCode = InviteCodeHash.generateInviteCode(email),
                            paired = paired
                        )
                    } else {
                        UserProfile(
                            userId = UidDecode.getUserNumericId(),
                            email = email,
                            userName = "",
                            imageUrl = "",
                            inviteCode = InviteCodeHash.generateInviteCode(email),
                            paired = false
                        )
                    }
                    callback(userProfile)
                }
                .addOnFailureListener { exception ->
                    errorCallback(exception)
                }
        }


    }

}