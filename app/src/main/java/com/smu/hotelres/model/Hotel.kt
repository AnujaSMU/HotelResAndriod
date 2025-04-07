package com.smu.hotelres.model

import java.io.Serializable

data class Hotel(
    val id: String,
    val name: String,
    val price: Double,
    val availability: Boolean
) : Serializable 