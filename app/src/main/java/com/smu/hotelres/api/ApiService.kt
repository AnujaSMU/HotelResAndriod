package com.smu.hotelres.api

import com.smu.hotelres.model.Hotel
import com.smu.hotelres.model.Reservation
import com.smu.hotelres.model.ReservationRequest
import com.smu.hotelres.model.ReservationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("available_hotels/")
    suspend fun getAvailableHotels(
        @Query("checkin") checkinDate: String,
        @Query("checkout") checkoutDate: String
    ): Response<List<Hotel>>

    @POST("reservation/")
    suspend fun createReservation(
        @Body request: ReservationRequest
    ): Response<ReservationResponse>
} 