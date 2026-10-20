package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RoomType(val displayNameSwahili: String, val displayNameEnglish: String) {
    SINGLE("Single (Kitanda Kimoja)", "Single Room"),
    DOUBLE("Double (Kitanda Kikubwa)", "Double Room"),
    VIP("VIP / Deluxe", "VIP / Deluxe Room"),
    SUITE("Executive Suite", "Executive Suite")
}

enum class RoomStatus(val swahili: String, val english: String) {
    VACANT("Wazi", "Vacant"),
    OCCUPIED("Kimechukuliwa", "Occupied"),
    CLEANING("Usafi", "Needs Cleaning"),
    MAINTENANCE("Matengenezo", "Maintenance")
}

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomNumber: String,
    val roomType: RoomType,
    val pricePerNight: Double,
    val status: RoomStatus = RoomStatus.VACANT,
    val floor: String = "Ghorofa ya Chini",
    val amenities: String = "AC, TV, WiFi, Choo cha Ndani",
    val notes: String = ""
)
