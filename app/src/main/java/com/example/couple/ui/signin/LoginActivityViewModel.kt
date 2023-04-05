package com.example.couple.ui.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginActivityViewModel : ViewModel(){
    var signInEmail = MutableLiveData<String>()
}