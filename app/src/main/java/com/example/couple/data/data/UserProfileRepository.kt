package com.example.couple.data.data

import android.app.Application
import android.content.Context
import com.example.couple.util.InviteCodeHash
import com.example.couple.util.UidDecode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

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

        suspend fun retrieveCurrentDays(context: Context): Deferred<Unit> = coroutineScope {
            async {
                val db = Firebase.firestore
                val pairedRef = db.collection("Paired")
                val currentUser = FirebaseAuth.getInstance().currentUser

                if (currentUser != null) {
                    pairedRef.whereEqualTo("user1.email", currentUser.email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val daysList = mutableListOf<Days>()
                                for (document in documents) {
                                    val list = document.get("days") as? List<HashMap<String, Any>>
                                    if (list != null) {
                                        for (item in list) {
                                            val days = Days(
                                                item["type"] as String,
                                                item["img"] as String,
                                                item["date"] as String
                                            )
                                            daysList.add(days)
                                        }
                                    }
                                }
                                saveDays(context, daysList)
                            } else {
                                pairedRef.whereEqualTo("user2.email", currentUser.email)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (!documents.isEmpty) {
                                            val daysList = mutableListOf<Days>()
                                            for (document in documents) {
                                                val list = document.get("days") as? List<HashMap<String, Any>>
                                                if (list != null) {
                                                    for (item in list) {
                                                        val days = Days(
                                                            item["type"] as String,
                                                            item["img"] as String,
                                                            item["date"] as String
                                                        )
                                                        daysList.add(days)
                                                    }
                                                }
                                            }
                                            saveDays(context, daysList)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle failure
                                    }
                            }
                        }
                        .await() // Wait for the database operation to complete
                }
            }
        }



    }

}