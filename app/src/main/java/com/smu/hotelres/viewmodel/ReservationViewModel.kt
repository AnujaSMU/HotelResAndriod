package com.smu.hotelres.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smu.hotelres.model.Guest
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.model.Reservation
import com.smu.hotelres.repository.HotelRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReservationViewModel(private val repository: HotelRepository) : ViewModel() {
    private val _reservationResult = MutableLiveData<Reservation?>()
    val reservationResult: MutableLiveData<Reservation?> = _reservationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun calculateTotalPrice(hotel: Hotel, checkInDate: String, checkOutDate: String, guests: Int): String {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val checkIn = dateFormat.parse(checkInDate)
        val checkOut = dateFormat.parse(checkOutDate)
        val diffInMillis = checkOut!!.time - checkIn!!.time
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        
        if (diffInDays > 100) {
            _error.value = "Bookings cannot exceed 100 days"
            return ""
        }
        
        val totalPrice = hotel.price * guests * maxOf(1, diffInDays)
        return "Price: $${hotel.price}/night\nStay Duration: $diffInDays night(s)\nTotal Price: $${totalPrice}"
    }

    fun formatAvailabilityDate(dateString: String?): String {
        return dateString?.let {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(it)
                date?.let { parsedDate ->
                    "Available until: ${outputFormat.format(parsedDate)}"
                } ?: ""
            } catch (e: Exception) {
                ""
            }
        } ?: ""
    }

    fun validateGuestInputs(guestInputs: List<Pair<String, String>>): Boolean {
        return guestInputs.all { (name, gender) ->
            name.isNotBlank() && gender.isNotBlank()
        }
    }

    fun submitReservation(
        hotel: Hotel,
        checkInDate: String,
        checkOutDate: String,
        guestInputs: List<Pair<String, String>>
    ) {
        _isLoading.value = true
        
        val guestList = guestInputs.map { (name, gender) ->
            Guest(guest_name = name, gender = gender)
        }

        viewModelScope.launch {
            try {
                val reservation = repository.createReservation(
                    hotel = hotel,
                    checkinDate = checkInDate,
                    checkoutDate = checkOutDate,
                    guests = guestList
                )

                if (reservation != null && 
                    reservation.confirmation_number.isNotEmpty() &&
                    reservation.hotel_name.isNotEmpty() && 
                    reservation.guests_list.isNotEmpty()) {
                    _reservationResult.value = reservation
                } else {
                    _error.value = "Reservation data is incomplete. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 