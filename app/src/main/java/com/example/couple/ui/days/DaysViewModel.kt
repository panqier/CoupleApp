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
                Days("hug","","2022-01-01"),
                Days("Pan birthday","","1997-06-21"),
                Days("first date","","2022-11-30"),
                Days("this year travel","","2023-11-01"),
                Days("vaccine","","2023-04-10")
            )
        )
    }
}