package com.example.couple.ui.days

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.couple.data.data.Days

class DaysViewModel : ViewModel() {

    private val _daysList = MutableLiveData<List<Days>>()
    val daysList : LiveData<List<Days>> = _daysList

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
}