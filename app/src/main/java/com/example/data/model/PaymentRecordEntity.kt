package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingId: Long,
    val guestName: String,
    val roomNumber: String,
    val amount: Double,
    val paymentMethod: String, // "Cash", "M-Pesa", "Tigo Pesa", "Airtel Money", "HaloPesa", "Benki / Kadi"
    val paymentDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
