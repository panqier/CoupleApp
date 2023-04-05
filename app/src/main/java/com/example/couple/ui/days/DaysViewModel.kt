package com.example.couple.ui.days

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.couple.data.data.days

class DaysViewModel : ViewModel() {

    private val _daysList = MutableLiveData<List<days>>()
    val daysList : LiveData<List<days>> = _daysList

    init {
        _daysList.postValue(
            listOf(
                days("hug","","2022-01-01"),
                days("Pan birthday","","1997-06-21"),
                days("first date","","2022-11-30"),
                days("this year travel","","2023-11-01"),
                days("vaccine","","2023-04-10")
            )
        )
    }
}