package com.example.couple.ui.days

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.couple.data.data.Days
import com.example.couple.data.data.saveStartDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DaysViewModel : ViewModel() {

    private val _daysList = MutableLiveData<List<Days>>()
    val daysList : LiveData<List<Days>> = _daysList

    val selectedDay = MutableLiveData<Days>()

    val selectedPosition = MutableLiveData<Int>()

    val isNavigateFromCard = MutableLiveData<Boolean>()

    fun setSelectedDay(day: Days) {
        selectedDay.value = day
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition.value = position
    }

    init {
        _daysList.postValue(
            listOf(
                Days("first met","","2023-01-01"),
                Days("Bob birthday","","2023-12-31"),
                Days("first date","","2023-01-01"),
                Days("this year travel","","2023-12-31"),
            )
        )
    }

    fun addNewDay(eventText: String, date: String, context: Context, navController: NavController) {
        val db = Firebase.firestore
        val pairedRef = db.collection("Paired")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            pairedRef.whereEqualTo("user1.email", currentUser.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            addNewDayDocument(eventText, date, context, navController, document)
                        }
                    } else {
                        pairedRef.whereEqualTo("user2.email", currentUser.email)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    for (document in documents) {
                                        addNewDayDocument(eventText, date, context, navController, document)
                                    }
                                }
                            }
                    }
                }
        }
    }

    fun updateDay(eventText: String, date: String, context: Context, navController: NavController) {
        val db = Firebase.firestore
        val pairedRef = db.collection("Paired")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            pairedRef.whereEqualTo("user1.email", currentUser.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            updateDayDocument(eventText, date, context, navController, document)
                        }
                    } else {
                        pairedRef.whereEqualTo("user2.email", currentUser.email)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    for (document in documents) {
                                        updateDayDocument(
                                            eventText,
                                            date,
                                            context,
                                            navController,
                                            document
                                        )
                                    }
                                }
                            }
                    }
                }
        }
    }

    fun updateStartDate(startDate: String, context: Context) {
        val db = Firebase.firestore
        val pairedRef = db.collection("Paired")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            pairedRef.whereEqualTo("user1.email", currentUser.email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            document.reference.update("startDate", startDate)
                                .addOnSuccessListener {
                                    // Handle success
                                    saveStartDate(context, startDate)
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                }
                        }
                    } else {
                        pairedRef.whereEqualTo("user2.email", currentUser.email)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    for (document in documents) {
                                        document.reference.update("startDate", startDate)
                                            .addOnSuccessListener {
                                                // Handle success
                                                saveStartDate(context, startDate)
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle failure
                                            }
                                    }
                                }
                            }
                    }
                }
        }
    }

    private fun updateDayDocument(eventText: String, date: String, context: Context, navController: NavController, document: QueryDocumentSnapshot) {
        val daysList = document.get("days") as? List<HashMap<String, Any>> ?: emptyList()
        if (daysList.isNotEmpty() && selectedPosition.value != null) {
            val updatedDaysList = daysList.map { Days(it["type"] as String, it["img"] as String, it["date"] as String) }.toMutableList()
            updatedDaysList[selectedPosition.value!!] = Days(eventText, "", date)
            document.reference.update("days", updatedDaysList.map { hashMapOf("type" to it.type, "img" to it.img, "date" to it.date) })
                .addOnSuccessListener {
                    // Update the local _daysList LiveData object with the new days list
                    _daysList.postValue(updatedDaysList)
                    Toast.makeText(context, "Days updated successfully", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating days: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



    private fun addNewDayDocument(eventText: String, date: String, context: Context, navController: NavController, document: QueryDocumentSnapshot) {
        val newDays = Days(eventText, "", date)
        val daysList = document.get("days") as? List<HashMap<String, Any>> ?: emptyList()
        val updatedList = daysList.map { Days(it["type"] as String, it["img"] as String, it["date"] as String) } + newDays
        document.reference.update("days", updatedList.map { hashMapOf("type" to it.type, "img" to it.img, "date" to it.date) })
            .addOnSuccessListener {
                Toast.makeText(context, "New days added successfully", Toast.LENGTH_SHORT).show()
                _daysList.postValue(updatedList)
                navController.navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding new days: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}