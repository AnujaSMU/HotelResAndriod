package com.smu.hotelres.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchViewModel : ViewModel() {
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun validateSearchInputs(
        checkInDate: String,
        checkOutDate: String,
        guests: String
    ): Boolean {
        if (checkInDate.isEmpty() || checkOutDate.isEmpty() || guests.isEmpty()) {
            _error.value = "Please fill in all fields"
            return false
        }

        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        try {
            val checkIn = dateFormat.parse(checkInDate)
            val checkOut = dateFormat.parse(checkOutDate)

            if (checkIn == null || checkOut == null) {
                _error.value = "Invalid date format"
                return false
            }

            if (checkIn.before(today)) {
                _error.value = "Check-in date cannot be in the past"
                return false
            }

            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                _error.value = "Check-out date must be after check-in date"
                return false
            }

            val guestsInt = guests.toInt()
            if (guestsInt < 1 || guestsInt > 10) {
                _error.value = "Number of guests must be between 1 and 10"
                return false
            }

            return true
        } catch (e: Exception) {
            _error.value = "Invalid input format"
            return false
        }
    }
} 