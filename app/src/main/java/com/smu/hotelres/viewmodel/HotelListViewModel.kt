package com.smu.hotelres.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.repository.HotelRepository
import kotlinx.coroutines.launch

class HotelListViewModel(private val repository: HotelRepository) : ViewModel() {
    private val _hotels = MutableLiveData<List<Hotel>>()
    val hotels: LiveData<List<Hotel>> = _hotels

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _selectedHotel = MutableLiveData<Hotel?>()
    val selectedHotel: LiveData<Hotel?> = _selectedHotel

    fun fetchHotels(checkInDate: String, checkOutDate: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val hotelsList = repository.getAvailableHotels(checkInDate, checkOutDate)
                _hotels.value = hotelsList
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load hotels: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun selectHotel(hotel: Hotel) {
        _selectedHotel.value = hotel
    }

    fun clearSelection() {
        _selectedHotel.value = null
    }
} 