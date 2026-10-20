package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus(val swahili: String, val english: String) {
    ACTIVE("Yupo Ndani", "Active Guest"),
    CHECKED_OUT("Ametoka", "Checked Out"),
    CANCELLED("Imefutwa", "Cancelled")
}

@Entity(tableName = "bookings")
data class GuestBookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val guestName: String,
    val phoneNumber: String,
    val idNumber: String, // NIDA, Passport, etc.
    val roomId: Long,
    val roomNumber: String,
    val checkInDate: Long,
    val expectedCheckOutDate: Long,
    val actualCheckOutDate: Long? = null,
    val daysCount: Int = 1,
    val totalAmount: Double,
    val amountPaid: Double,
    val paymentMethod: String = "Cash",
    val status: BookingStatus = BookingStatus.ACTIVE,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
