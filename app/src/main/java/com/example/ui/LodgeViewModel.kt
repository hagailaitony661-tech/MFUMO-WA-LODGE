package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.LodgeDatabase
import com.example.data.model.BookingStatus
import com.example.data.model.GuestBookingEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomType
import com.example.data.repository.LodgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class LodgeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LodgeDatabase.getDatabase(application)
    private val repository = LodgeRepository(
        database.roomDao(),
        database.bookingDao(),
        database.paymentDao()
    )

    // Language state: true = Kiswahili (default as requested), false = English
    val isSwahili = MutableStateFlow(true)

    // Search and filters
    val roomSearchQuery = MutableStateFlow("")
    val roomFilterStatus = MutableStateFlow<RoomStatus?>(null)
    val roomFilterType = MutableStateFlow<RoomType?>(null)
    val guestSearchQuery = MutableStateFlow("")

    // Raw database flows
    val allRooms: StateFlow<List<RoomEntity>> = repository.allRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBookings: StateFlow<List<GuestBookingEntity>> = repository.activeBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<GuestBookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentRecordEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback messages / Snackbar
    val message = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun toggleLanguage() {
        isSwahili.value = !isSwahili.value
    }

    fun clearMessage() {
        message.value = null
    }

    // Filtered rooms
    val filteredRooms: StateFlow<List<RoomEntity>> = combine(
        allRooms,
        roomSearchQuery,
        roomFilterStatus,
        roomFilterType
    ) { rooms, query, status, type ->
        rooms.filter { room ->
            val matchesQuery = query.isBlank() ||
                    room.roomNumber.contains(query, ignoreCase = true) ||
                    room.floor.contains(query, ignoreCase = true) ||
                    room.amenities.contains(query, ignoreCase = true)
            val matchesStatus = status == null || room.status == status
            val matchesType = type == null || room.roomType == type
            matchesQuery && matchesStatus && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered active guests
    val filteredActiveBookings: StateFlow<List<GuestBookingEntity>> = combine(
        activeBookings,
        guestSearchQuery
    ) { bookings, query ->
        if (query.isBlank()) bookings
        else bookings.filter {
            it.guestName.contains(query, ignoreCase = true) ||
                    it.phoneNumber.contains(query, ignoreCase = true) ||
                    it.roomNumber.contains(query, ignoreCase = true) ||
                    it.idNumber.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered all bookings history
    val filteredAllBookings: StateFlow<List<GuestBookingEntity>> = combine(
        allBookings,
        guestSearchQuery
    ) { bookings, query ->
        if (query.isBlank()) bookings
        else bookings.filter {
            it.guestName.contains(query, ignoreCase = true) ||
                    it.phoneNumber.contains(query, ignoreCase = true) ||
                    it.roomNumber.contains(query, ignoreCase = true) ||
                    it.idNumber.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's revenue calculation
    val todayRevenue: StateFlow<Double> = allPayments.combine(isSwahili) { payments, _ ->
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        payments.filter { it.paymentDate >= startOfToday }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalRevenue: StateFlow<Double> = allPayments.combine(isSwahili) { payments, _ ->
        payments.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addNewRoom(
        roomNumber: String,
        roomType: RoomType,
        pricePerNight: Double,
        floor: String,
        amenities: String
    ) {
        viewModelScope.launch {
            if (roomNumber.isBlank()) {
                message.value = if (isSwahili.value) "Tafadhali weka namba ya chumba" else "Please enter room number"
                return@launch
            }
            val existing = allRooms.value.any { it.roomNumber.equals(roomNumber.trim(), ignoreCase = true) }
            if (existing) {
                message.value = if (isSwahili.value) "Chumba #$roomNumber kipo tayari!" else "Room #$roomNumber already exists!"
                return@launch
            }

            val newRoom = RoomEntity(
                roomNumber = roomNumber.trim(),
                roomType = roomType,
                pricePerNight = pricePerNight,
                status = RoomStatus.VACANT,
                floor = floor.ifBlank { "Ghorofa ya 1" },
                amenities = amenities.ifBlank { "WiFi, AC, TV" }
            )
            repository.addRoom(newRoom)
            message.value = if (isSwahili.value) "Chumba #$roomNumber kimeongezwa kikamilifu!" else "Room #$roomNumber added successfully!"
        }
    }

    fun updateRoomStatus(roomId: Long, newStatus: RoomStatus) {
        viewModelScope.launch {
            repository.updateRoomStatus(roomId, newStatus)
            message.value = if (isSwahili.value) "Hali ya chumba imesasishwa kuwa: ${newStatus.swahili}" else "Room status updated to: ${newStatus.english}"
        }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch {
            if (room.status == RoomStatus.OCCUPIED) {
                message.value = if (isSwahili.value) "Huwezi kufuta chumba kilicho na mteja!" else "Cannot delete an occupied room!"
                return@launch
            }
            repository.deleteRoom(room)
            message.value = if (isSwahili.value) "Chumba #${room.roomNumber} kimefutwa" else "Room #${room.roomNumber} deleted"
        }
    }

    fun checkInGuest(
        guestName: String,
        phoneNumber: String,
        idNumber: String,
        room: RoomEntity,
        daysCount: Int,
        totalAmount: Double,
        amountPaid: Double,
        paymentMethod: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (guestName.isBlank() || phoneNumber.isBlank() || idNumber.isBlank()) {
                message.value = if (isSwahili.value) "Tafadhali jaza Jina, Simu, na Kitambulisho" else "Please fill Name, Phone, and ID"
                return@launch
            }

            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, daysCount.coerceAtLeast(1))
            val expectedCheckOut = cal.timeInMillis

            repository.checkInGuest(
                guestName = guestName.trim(),
                phoneNumber = phoneNumber.trim(),
                idNumber = idNumber.trim(),
                roomId = room.id,
                roomNumber = room.roomNumber,
                checkInDate = now,
                expectedCheckOutDate = expectedCheckOut,
                daysCount = daysCount,
                totalAmount = totalAmount,
                amountPaid = amountPaid,
                paymentMethod = paymentMethod,
                notes = notes
            )

            message.value = if (isSwahili.value)
                "Mteja $guestName amesajiliwa katika Chumba #${room.roomNumber}!"
            else
                "Guest $guestName checked into Room #${room.roomNumber}!"

            onSuccess()
        }
    }

    fun checkOutGuest(
        booking: GuestBookingEntity,
        nextRoomStatus: RoomStatus = RoomStatus.CLEANING,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.checkOutGuest(booking, nextRoomStatus)
            message.value = if (isSwahili.value)
                "Mteja ${booking.guestName} amefanyiwa Check-Out. Chumba #${booking.roomNumber} sasa ni: ${nextRoomStatus.swahili}"
            else
                "Guest ${booking.guestName} checked out. Room #${booking.roomNumber} is now: ${nextRoomStatus.english}"

            onSuccess()
        }
    }

    fun addPayment(
        booking: GuestBookingEntity,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (amount <= 0) return@launch
            repository.recordExtraPayment(
                bookingId = booking.id,
                guestName = booking.guestName,
                roomNumber = booking.roomNumber,
                amount = amount,
                paymentMethod = paymentMethod,
                notes = notes
            )
            message.value = if (isSwahili.value) "Malipo ya ziada yamerekodiwa!" else "Additional payment recorded!"
        }
    }
}
