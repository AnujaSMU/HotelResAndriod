package com.smu.hotelres.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smu.hotelres.repository.HotelRepository

class HotelListViewModelFactory(
    private val repository: HotelRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelListViewModel::class.java)) {
            return HotelListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 