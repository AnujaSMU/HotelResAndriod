package com.smu.hotelres.model

import java.io.Serializable
import java.util.Date

data class Hotel(
    val id: Int,
    val name: String,
    val rating: Double,
    val price: Int,
    val availableUntil: Date? = null,
    val available: Boolean
) : Serializable 