package com.example.couple.ui.my

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.couple.data.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MyViewModel : ViewModel() {

    private val _isDataFieldRetrieved = MutableLiveData<Boolean>()
    val isDataFieldRetrieved: MutableLiveData<Boolean> = _isDataFieldRetrieved

    private val _isPaired = MutableLiveData<Boolean>()
    val isPaired: MutableLiveData<Boolean> = _isPaired

    var dataFieldVal : String = " "
    val userName = MutableLiveData(FirebaseAuth.getInstance().currentUser?.displayName)
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val _userProfile = MutableLiveData<UserProfile>()

    fun setUserProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
    }

    fun getUserProfile(): LiveData<UserProfile> {
        return _userProfile
    }

    fun getDataFiledFromFirestore(dataField: String) {
        val db  = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val docRef = currentUser?.let { db.collection("User").document(it.uid) }
        docRef?.get()?.addOnSuccessListener { document ->
            if(document != null){
                val fieldData = document.getString(dataField)
                Log.d("TAG", "DataField: $fieldData")
                _isDataFieldRetrieved.postValue(true)
                if (fieldData != null) {
                    dataFieldVal = fieldData
                }
            } else {
                Log.d("TAG", "Can not find the document")
                _isDataFieldRetrieved.postValue(false)
            }
        }?.addOnFailureListener { exception ->
            Log.d("TAG", "Fetch the document failure", exception)
            _isDataFieldRetrieved.postValue(false)
        }
    }

    fun updateUserName(userName: String) {
        val db  = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val docRef = currentUser?.let { db.collection("User").document(it.uid) }
        docRef?.update("userName", userName)?.addOnSuccessListener {
            Log.d("TAG", "Update pair status successfully")
        }?.addOnFailureListener {
            Log.d("TAG", "Update pair status failure")
        }
    }

    fun updatePairStatus(isPaired: Boolean) {
        val db  = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val docRef = currentUser?.let { db.collection("User").document(it.uid) }
        docRef?.update("paired", isPaired)?.addOnSuccessListener {
            _isPaired.postValue(isPaired)
            Log.d("TAG", "Update pair status successfully")
        }?.addOnFailureListener {
            Log.d("TAG", "Update pair status failure")
        }
    }

    fun uploadUserImage(imageUrl: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("user/$userId.jpg")

        // Upload the file to Firebase Storage
        storageRef.putFile(imageUrl)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL from the task snapshot
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        // Update the user's photoUrl field with the download URL
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build()

                        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("TAG", "User profile photo updated.")
                                } else {
                                    Log.d("TAG", "User profile photo update failed.")
                                }
                            }
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Upload failed: $exception")
            }
    }


    fun getPairedStatus() {
        val db = Firebase.firestore
        val userCollection = db.collection("User")
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            userCollection.document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val paired = document.getBoolean("paired")
                        if (paired!= null) {
                            // fieldValue will be true or false
                            if(paired == true) _isPaired.postValue(true)
                            else _isPaired.postValue(false)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }
        }

    }

}