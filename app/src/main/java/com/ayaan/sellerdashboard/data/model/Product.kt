package com.ayaan.sellerdashboard.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val vendorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val email:String=""
)

