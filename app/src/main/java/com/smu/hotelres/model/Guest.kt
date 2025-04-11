package com.smu.hotelres.model

import java.io.Serializable

data class Guest(
    val guest_name: String,
    val gender: String
) : Serializable 