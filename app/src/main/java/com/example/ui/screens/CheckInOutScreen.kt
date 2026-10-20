package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuestBookingEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.ui.LodgeViewModel
import com.example.ui.components.RoomTypeBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatMoney
import com.example.ui.theme.GreenVacant
import com.example.ui.theme.RedOccupied

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInOutScreen(
    viewModel: LodgeViewModel,
    modifier: Modifier = Modifier
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val allRooms by viewModel.allRooms.collectAsState()
    val activeBookings by viewModel.filteredActiveBookings.collectAsState()
    val guestSearch by viewModel.guestSearchQuery.collectAsState()

    val vacantRooms = remember(allRooms) {
        allRooms.filter { it.status == RoomStatus.VACANT }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    var bookingToCheckOut by remember { mutableStateOf<GuestBookingEntity?>(null) }

    // Check-in Form States
    var guestName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var selectedRoom by remember { mutableStateOf<RoomEntity?>(null) }
    var daysCountText by remember { mutableStateOf("1") }
    var amountPaidText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("M-Pesa") }
    var notes by remember { mutableStateOf("") }
    var showRoomPicker by remember { mutableStateOf(false) }

    val days = daysCountText.toIntOrNull() ?: 1
    val calculatedTotal = (selectedRoom?.pricePerNight ?: 0.0) * days

    // Auto update amount paid with total initially if empty
    LaunchedEffect(calculatedTotal) {
        if (amountPaidText.isEmpty() && calculatedTotal > 0) {
            amountPaidText = calculatedTotal.toLong().toString()
        }
    }

    // Set first vacant room default
    LaunchedEffect(vacantRooms) {
        if (selectedRoom == null && vacantRooms.isNotEmpty()) {
            selectedRoom = vacantRooms.first()
        }
    }

    val paymentMethods = listOf("Cash (Pesa Taslimu)", "M-Pesa", "Tigo Pesa", "Airtel Money", "HaloPesa", "Benki / Kadi")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("check_in_out_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = if (isSwahili) "📝 Sajili Mteja (Check-In)" else "📝 Check-In New Guest",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = if (isSwahili) "👥 Waliopo Ndani (${activeBookings.size})" else "👥 Active Guests (${activeBookings.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (selectedTabIndex == 0) {
            // Check-in Form Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = if (isSwahili) "Fomu ya Kusajili Mteja Anayeingia" else "Guest Check-In Form",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSwahili)
                            "Jaza taarifa zote za mteja. Chumba kitabadilishwa kuwa 'Kimechukuliwa' mara moja."
                        else
                            "Fill in guest details. Selected room will be updated to 'Occupied' automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Guest Details Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isSwahili) "👤 Taarifa za Mteja" else "👤 Guest Information",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = guestName,
                                onValueChange = { guestName = it },
                                label = { Text(if (isSwahili) "Jina Kamili la Mteja*" else "Full Name*") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkin_guest_name")
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text(if (isSwahili) "Namba ya Simu (mf. 07XXXXXXXX)*" else "Phone Number*") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkin_phone_number")
                            )

                            OutlinedTextField(
                                value = idNumber,
                                onValueChange = { idNumber = it },
                                label = { Text(if (isSwahili) "Namba ya Kitambulisho / NIDA / Pasipoti*" else "National ID / Passport Number*") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkin_id_number")
                            )
                        }
                    }
                }

                // Room Selection Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isSwahili) "🛏️ Chagua Chumba Kilicho Wazi" else "🛏️ Select Vacant Room",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            if (vacantRooms.isEmpty()) {
                                Surface(
                                    color = RedOccupied.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = RedOccupied)
                                        Text(
                                            text = if (isSwahili)
                                                "⚠️ Hakuna chumba kilicho wazi kwa sasa!"
                                            else
                                                "⚠️ No vacant rooms available right now!",
                                            color = RedOccupied,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showRoomPicker = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = if (selectedRoom != null)
                                                    "${if (isSwahili) "Chumba" else "Room"} #${selectedRoom!!.roomNumber}"
                                                else
                                                    if (isSwahili) "Bonyeza kuchagua chumba" else "Tap to choose a room",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            if (selectedRoom != null) {
                                                Text(
                                                    text = "${selectedRoom!!.floor} • ${formatMoney(selectedRoom!!.pricePerNight)}/usiku",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = daysCountText,
                                    onValueChange = {
                                        daysCountText = it
                                        val d = it.toIntOrNull() ?: 1
                                        val total = (selectedRoom?.pricePerNight ?: 0.0) * d
                                        amountPaidText = total.toLong().toString()
                                    },
                                    label = { Text(if (isSwahili) "Idadi ya Siku" else "Number of Days") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = if (isSwahili) "Jumla ya Gharama:" else "Total Cost:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatMoney(calculatedTotal),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Payment Details Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isSwahili) "💵 Malipo na Njia ya Malipo" else "💵 Payment Details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = amountPaidText,
                                onValueChange = { amountPaidText = it },
                                label = { Text(if (isSwahili) "Kiasi Kilicholipwa (TZS)*" else "Amount Paid (TZS)*") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkin_amount_paid")
                            )

                            val paid = amountPaidText.toDoubleOrNull() ?: 0.0
                            val balance = calculatedTotal - paid
                            if (balance > 0) {
                                Text(
                                    text = "⚠️ ${if (isSwahili) "Baki ya Kulipwa Baadaye:" else "Remaining Balance:"} ${formatMoney(balance)}",
                                    color = RedOccupied,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            } else {
                                Text(
                                    text = "✅ ${if (isSwahili) "Imelipwa Kamili" else "Paid in Full"}",
                                    color = GreenVacant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = if (isSwahili) "Njia ya Malipo:" else "Payment Method:",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                paymentMethods.chunked(2).forEach { rowMethods ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowMethods.forEach { method ->
                                            FilterChip(
                                                selected = paymentMethod == method,
                                                onClick = { paymentMethod = method },
                                                label = { Text(method, fontSize = 12.sp) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowMethods.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text(if (isSwahili) "Maelezo ya Ziada (Hiari)" else "Additional Notes (Optional)") },
                                singleLine = false,
                                maxLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Submit Button
                item {
                    Button(
                        onClick = {
                            if (selectedRoom != null) {
                                val paid = amountPaidText.toDoubleOrNull() ?: calculatedTotal
                                viewModel.checkInGuest(
                                    guestName = guestName,
                                    phoneNumber = phoneNumber,
                                    idNumber = idNumber,
                                    room = selectedRoom!!,
                                    daysCount = days,
                                    totalAmount = calculatedTotal,
                                    amountPaid = paid,
                                    paymentMethod = paymentMethod,
                                    notes = notes,
                                    onSuccess = {
                                        guestName = ""
                                        phoneNumber = ""
                                        idNumber = ""
                                        notes = ""
                                        selectedTabIndex = 1
                                    }
                                )
                            }
                        },
                        enabled = guestName.isNotBlank() && phoneNumber.isNotBlank() && idNumber.isNotBlank() && selectedRoom != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("submit_checkin_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSwahili) "Kamilisha Usajili (Check-in)" else "Complete Check-in",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        } else {
            // Tab 2: Active Guests & Check-out List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = guestSearch,
                    onValueChange = { viewModel.guestSearchQuery.value = it },
                    placeholder = {
                        Text(if (isSwahili) "Tafuta mteja (Jina, Simu, Chumba)..." else "Search guest (Name, Phone, Room)...")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (guestSearch.isNotEmpty()) {
                            IconButton(onClick = { viewModel.guestSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (activeBookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isSwahili) "Hakuna mteja anayeishi ndani kwa sasa" else "No guests checked in currently",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { selectedTabIndex = 0 }) {
                                Text(if (isSwahili) "Sajili Mteja Mpya" else "Check In New Guest")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(activeBookings) { booking ->
                            ActiveGuestCard(
                                booking = booking,
                                isSwahili = isSwahili,
                                onCheckOutClick = { bookingToCheckOut = booking }
                            )
                        }
                    }
                }
            }
        }
    }

    // Room Picker Dialog
    if (showRoomPicker) {
        AlertDialog(
            onDismissRequest = { showRoomPicker = false },
            title = {
                Text(
                    text = if (isSwahili) "Chagua Chumba Kilicho Wazi" else "Select Vacant Room",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vacantRooms) { room ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRoom = room
                                    val d = daysCountText.toIntOrNull() ?: 1
                                    val total = room.pricePerNight * d
                                    amountPaidText = total.toLong().toString()
                                    showRoomPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRoom?.id == room.id)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${if (isSwahili) "Chumba" else "Room"} #${room.roomNumber}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${room.floor} • ${room.roomType.displayNameSwahili.substringBefore(" (")}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = formatMoney(room.pricePerNight),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoomPicker = false }) {
                    Text(if (isSwahili) "Funga" else "Close")
                }
            }
        )
    }

    // Checkout Confirmation Dialog
    if (bookingToCheckOut != null) {
        val booking = bookingToCheckOut!!
        val balance = booking.totalAmount - booking.amountPaid

        AlertDialog(
            onDismissRequest = { bookingToCheckOut = null },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = RedOccupied) },
            title = {
                Text(
                    text = if (isSwahili) "Thibitisha Check-Out" else "Confirm Check-Out",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isSwahili)
                            "Unathibitisha kutoa mteja huyu kwenye mfumo na kuruhusu chumba kiandaliwe:"
                        else
                            "Confirm check out for this guest and free up the room:"
                    )
                    Divider()
                    Text("• ${if (isSwahili) "Mteja" else "Guest"}: ${booking.guestName}")
                    Text("• ${if (isSwahili) "Chumba" else "Room"}: #${booking.roomNumber}")
                    Text("• ${if (isSwahili) "Gharama Kamili" else "Total Cost"}: ${formatMoney(booking.totalAmount)}")
                    Text("• ${if (isSwahili) "Imelipwa" else "Paid"}: ${formatMoney(booking.amountPaid)}")
                    if (balance > 0) {
                        Text(
                            "⚠️ ${if (isSwahili) "Baki ya Kulipwa" else "Balance Due"}: ${formatMoney(balance)}",
                            color = RedOccupied,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "✅ ${if (isSwahili) "Amelipa Kamili" else "Fully Settled"}",
                            color = GreenVacant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSwahili)
                            "Hali ya Chumba #${booking.roomNumber} itawekwa kuwa 'Wazi' (Vacant) tayari kupokea mteja mwingine."
                        else
                            "Room #${booking.roomNumber} status will be set to 'Vacant' ready for the next check-in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.checkOutGuest(booking, RoomStatus.VACANT) {
                            bookingToCheckOut = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedOccupied)
                ) {
                    Text(if (isSwahili) "Check-Out Sasa" else "Check Out Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCheckOut = null }) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        )
    }
}
