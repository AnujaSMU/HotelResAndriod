package com.smu.hotelres.model

import java.io.Serializable

data class Hotel(
    val id: Int,
    val name: String,
    val rating: Double,
    val price: Int,
    val available_until: String? = null,
    val available: Boolean
) : Serializable 