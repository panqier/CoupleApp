package com.example.couple.ui.my

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyViewModel : ViewModel() {

    private val _isDataFieldRetrieved = MutableLiveData<Boolean>()
    val isDataFieldRetrieved: MutableLiveData<Boolean> = _isDataFieldRetrieved

    var dataFieldVal: String = ""


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

}