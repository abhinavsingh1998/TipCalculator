package com.example.tipcalculator.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TipCalculatorViewModel: ViewModel() {

    val billAmountMutableLiveData = MutableLiveData<Double>()
    val billAmountOriginalMutableLiveData = MutableLiveData<Double>()
}