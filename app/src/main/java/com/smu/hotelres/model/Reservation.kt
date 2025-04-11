package com.smu.hotelres.model

import java.io.Serializable
import java.util.Date

data class Reservation(
    val confirmation_number: String,
    val hotel_name: String,
    val checkin: Date,
    val checkout: Date,
    val guests: List<Guest>
) : Serializable 