package com.example.data.repository

import com.example.data.db.BookingDao
import com.example.data.db.PaymentDao
import com.example.data.db.RoomDao
import com.example.data.model.BookingStatus
import com.example.data.model.GuestBookingEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class LodgeRepository(
    private val roomDao: RoomDao,
    private val bookingDao: BookingDao,
    private val paymentDao: PaymentDao
) {
    val allRooms: Flow<List<RoomEntity>> = roomDao.getAllRooms()
    val allBookings: Flow<List<GuestBookingEntity>> = bookingDao.getAllBookings()
    val activeBookings: Flow<List<GuestBookingEntity>> = bookingDao.getBookingsByStatus(BookingStatus.ACTIVE)
    val allPayments: Flow<List<PaymentRecordEntity>> = paymentDao.getAllPayments()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (roomDao.getRoomCount() == 0) {
            val initialRooms = listOf(
                RoomEntity(
                    roomNumber = "101",
                    roomType = RoomType.SINGLE,
                    pricePerNight = 35000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 1",
                    amenities = "WiFi, Feni, Choo cha Ndani"
                ),
                RoomEntity(
                    roomNumber = "102",
                    roomType = RoomType.SINGLE,
                    pricePerNight = 35000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 1",
                    amenities = "WiFi, AC, TV, Choo cha Ndani"
                ),
                RoomEntity(
                    roomNumber = "103",
                    roomType = RoomType.DOUBLE,
                    pricePerNight = 60000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 1",
                    amenities = "WiFi, AC, Smart TV, Fridge Ndogo"
                ),
                RoomEntity(
                    roomNumber = "201",
                    roomType = RoomType.DOUBLE,
                    pricePerNight = 60000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 2",
                    amenities = "WiFi, AC, Smart TV, Balcony"
                ),
                RoomEntity(
                    roomNumber = "202",
                    roomType = RoomType.VIP,
                    pricePerNight = 120000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 2",
                    amenities = "King Bed, AC, 55' TV, Jacuzzi, WiFi, Mini Bar"
                ),
                RoomEntity(
                    roomNumber = "301",
                    roomType = RoomType.SUITE,
                    pricePerNight = 180000.0,
                    status = RoomStatus.VACANT,
                    floor = "Ghorofa ya 3 (Penthouse)",
                    amenities = "Sitting Lounge, King Bed, Bathtub, City View, Kitchenette"
                )
            )
            roomDao.insertAllRooms(initialRooms)
            // Zero demo bookings and zero demo payments for clean commercial deployment!
        }
    }

    suspend fun addRoom(room: RoomEntity): Long = withContext(Dispatchers.IO) {
        roomDao.insertRoom(room)
    }

    suspend fun updateRoomStatus(roomId: Long, newStatus: RoomStatus) = withContext(Dispatchers.IO) {
        roomDao.updateRoomStatus(roomId, newStatus)
    }

    suspend fun updateRoomStatusByNumber(roomNumber: String, newStatus: RoomStatus) = withContext(Dispatchers.IO) {
        roomDao.updateRoomStatusByNumber(roomNumber, newStatus)
    }

    suspend fun deleteRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        roomDao.deleteRoom(room)
    }

    suspend fun checkInGuest(
        guestName: String,
        phoneNumber: String,
        idNumber: String,
        roomId: Long,
        roomNumber: String,
        checkInDate: Long,
        expectedCheckOutDate: Long,
        daysCount: Int,
        totalAmount: Double,
        amountPaid: Double,
        paymentMethod: String,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val booking = GuestBookingEntity(
            guestName = guestName,
            phoneNumber = phoneNumber,
            idNumber = idNumber,
            roomId = roomId,
            roomNumber = roomNumber,
            checkInDate = checkInDate,
            expectedCheckOutDate = expectedCheckOutDate,
            daysCount = daysCount,
            totalAmount = totalAmount,
            amountPaid = amountPaid,
            paymentMethod = paymentMethod,
            status = BookingStatus.ACTIVE,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )
        val bookingId = bookingDao.insertBooking(booking)

        // Mark room as occupied
        roomDao.updateRoomStatus(roomId, RoomStatus.OCCUPIED)

        // Record payment if paid
        if (amountPaid > 0) {
            val payment = PaymentRecordEntity(
                bookingId = bookingId,
                guestName = guestName,
                roomNumber = roomNumber,
                amount = amountPaid,
                paymentMethod = paymentMethod,
                paymentDate = System.currentTimeMillis(),
                notes = "Malipo ya Usajili / Check-in"
            )
            paymentDao.insertPayment(payment)
        }

        bookingId
    }

    suspend fun checkOutGuest(booking: GuestBookingEntity, nextRoomStatus: RoomStatus = RoomStatus.VACANT) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        bookingDao.updateBookingStatus(booking.id, BookingStatus.CHECKED_OUT, now)
        roomDao.updateRoomStatusByNumber(booking.roomNumber, nextRoomStatus)
    }

    suspend fun recordExtraPayment(
        bookingId: Long,
        guestName: String,
        roomNumber: String,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val payment = PaymentRecordEntity(
            bookingId = bookingId,
            guestName = guestName,
            roomNumber = roomNumber,
            amount = amount,
            paymentMethod = paymentMethod,
            paymentDate = System.currentTimeMillis(),
            notes = notes
        )
        paymentDao.insertPayment(payment)

        val booking = bookingDao.getBookingById(bookingId)
        if (booking != null) {
            val updated = booking.copy(amountPaid = booking.amountPaid + amount)
            bookingDao.updateBooking(updated)
        }
    }
}
