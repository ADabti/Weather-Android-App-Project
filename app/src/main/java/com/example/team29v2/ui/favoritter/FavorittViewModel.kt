package com.example.team29v2.ui.favoritter

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team29v2.data.DataSource
import com.example.team29v2.data.Tseries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavorittViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val data : DataSource = DataSource()

    private val badeLive : MutableLiveData<MutableList<Tseries>> by lazy {
        MutableLiveData<MutableList<Tseries>>()
    }

    fun getBader(): MutableLiveData<MutableList<Tseries>>{
        return badeLive
    }

    fun fetchBader(){
        viewModelScope.launch(Dispatchers.IO) {
            data.fetchBadeTillegg().also {
                badeLive.postValue(it)
            }
        }

    }
}