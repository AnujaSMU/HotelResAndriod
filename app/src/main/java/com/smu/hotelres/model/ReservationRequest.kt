package com.smu.hotelres.model

data class ReservationRequest(
    val hotel_id: Int,
    val hotel_name: String,
    val checkin: String,
    val checkout: String,
    val guests: List<Guest>
) 