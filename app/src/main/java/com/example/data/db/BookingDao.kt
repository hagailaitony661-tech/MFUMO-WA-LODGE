package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingStatus
import com.example.data.model.GuestBookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<GuestBookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY createdAt DESC")
    fun getBookingsByStatus(status: BookingStatus): Flow<List<GuestBookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Long): GuestBookingEntity?

    @Query("SELECT * FROM bookings WHERE roomNumber = :roomNumber AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveBookingForRoom(roomNumber: String): GuestBookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: GuestBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBookings(bookings: List<GuestBookingEntity>)

    @Update
    suspend fun updateBooking(booking: GuestBookingEntity)

    @Query("UPDATE bookings SET status = :newStatus, actualCheckOutDate = :checkOutDate WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Long, newStatus: BookingStatus, checkOutDate: Long)

    @Query("SELECT COUNT(*) FROM bookings WHERE status = 'ACTIVE'")
    fun getActiveBookingsCount(): Flow<Int>
}
