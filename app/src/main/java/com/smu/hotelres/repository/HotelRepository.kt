package com.smu.hotelres.repository

import android.util.Log
import com.smu.hotelres.api.RetrofitClient
import com.smu.hotelres.model.Guest
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.model.Reservation
import com.smu.hotelres.model.ReservationRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class HotelRepository {
    private val TAG = "HotelRepository"
    private val apiService = RetrofitClient.apiService
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun getAvailableHotels(checkInDate: String, checkOutDate: String): List<Hotel> {
        try {
            val response = apiService.getAvailableHotels(checkInDate, checkOutDate)
            if (response.isSuccessful) {
                Log.d(TAG, "API call successful: ${response.code()} - Found ${response.body()?.size ?: 0} hotels")
                return response.body() ?: emptyList()
            } else {
                Log.e(TAG, "API call failed with code: ${response.code()}, message: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call: ${e.message}", e)
            e.printStackTrace()
        }
        
        Log.d(TAG, "Returning mock hotel data")
        // If API call fails, return mock data for now
        return getMockHotels()
    }

    suspend fun createReservation(
        hotel: Hotel,
        checkinDate: String,
        checkoutDate: String,
        guests: List<Guest>
    ): Reservation? {
        val request = ReservationRequest(
            hotel_id = hotel.id,
            hotel_name = hotel.name,
            checkin = checkinDate,
            checkout = checkoutDate,
            guests_list = guests
        )

        try {
            Log.d(TAG, "Sending reservation request: $request")
            val response = apiService.createReservation(request)
            
            if (response.isSuccessful) {
                val reservationResponse = response.body()
                Log.d(TAG, "API call successful: ${response.code()} - Reservation: ${reservationResponse?.confirmation_number}")
                
                if (reservationResponse != null) {
                    // Create a full Reservation object with the confirmation number and request data
                    return constructReservationFromResponse(
                        confirmationNumber = reservationResponse.confirmation_number,
                        hotelName = hotel.name,
                        checkinDate = checkinDate,
                        checkoutDate = checkoutDate,
                        guests = guests
                    )
                }
            } else {
                Log.e(TAG, "API call failed with code: ${response.code()}, message: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during reservation API call: ${e.message}", e)
            e.printStackTrace()
        }
        
        // If API fails, return a mock reservation for testing
        Log.d(TAG, "Returning mock reservation data")
        return createMockReservation(hotel, checkinDate, checkoutDate, guests)
    }

    private fun constructReservationFromResponse(
        confirmationNumber: String,
        hotelName: String,
        checkinDate: String,
        checkoutDate: String,
        guests: List<Guest>
    ): Reservation {
        // Parse the dates
        val checkin = try {
            dateFormat.parse(checkinDate)
        } catch (e: Exception) {
            Calendar.getInstance().time
        }
        
        val checkout = try {
            dateFormat.parse(checkoutDate)
        } catch (e: Exception) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, 3)
            cal.time
        }
        
        return Reservation(
            confirmation_number = confirmationNumber,
            hotel_name = hotelName,
            checkin = checkin,
            checkout = checkout,
            guests_list = guests
        )
    }

    private fun createMockReservation(
        hotel: Hotel,
        checkinDate: String,
        checkoutDate: String,
        guests: List<Guest>
    ): Reservation {
        // Generate a random confirmation number
        val confirmationNumber = "RES-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        
        return constructReservationFromResponse(
            confirmationNumber = confirmationNumber,
            hotelName = hotel.name,
            checkinDate = checkinDate,
            checkoutDate = checkoutDate,
            guests = guests
        )
    }

    // Provide mock data for testing or when API is unavailable
    private fun getMockHotels(): List<Hotel> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val futureDate = calendar.time
        
        return listOf(
            Hotel(1, "Grand Hotel", 4.5, 199, futureDate, true),
            Hotel(2, "Seaside Resort", 4.8, 249, futureDate, true),
            Hotel(3, "Mountain View Inn", 3.7, 179, futureDate, false),
            Hotel(4, "City Center Hotel", 4.2, 299, futureDate, true),
            Hotel(5, "Luxury Palace", 4.9, 399, futureDate, true)
        )
    }
} 