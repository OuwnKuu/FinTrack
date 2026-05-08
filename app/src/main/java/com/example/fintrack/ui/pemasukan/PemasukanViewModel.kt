package com.example.fintrack.ui.pemasukan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PemasukanViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is pemasukan Fragment"
    }
    val text: LiveData<String> = _text
}