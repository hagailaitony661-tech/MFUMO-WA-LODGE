package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuestBookingEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.ui.LodgeViewModel
import com.example.ui.components.RoomTypeBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatMoney
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GreenVacant
import com.example.ui.theme.OrangeCleaning
import com.example.ui.theme.RedOccupied

@Composable
fun DashboardScreen(
    viewModel: LodgeViewModel,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToRooms: () -> Unit,
    onNavigateToReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val rooms by viewModel.allRooms.collectAsState()
    val activeBookings by viewModel.activeBookings.collectAsState()
    val todayRevenue by viewModel.todayRevenue.collectAsState()

    val totalRooms = rooms.size
    val occupiedRooms = rooms.count { it.status == RoomStatus.OCCUPIED }
    val vacantRooms = rooms.count { it.status == RoomStatus.VACANT }
    val cleaningRooms = rooms.count { it.status == RoomStatus.CLEANING }
    val occupancyRate = if (totalRooms > 0) ((occupiedRooms.toDouble() / totalRooms) * 100).toInt() else 0

    var guestForCheckOut by remember { mutableStateOf<GuestBookingEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Date Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSwahili) "Karibu kwenye Lodgi" else "Welcome to Lodge Manager",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSwahili)
                                "Muhtasari wa uendeshaji, vyumba, na mapato ya leo."
                            else
                                "Daily operations summary, room availability, and revenue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Summary KPI Grid
        item {
            Text(
                text = if (isSwahili) "📊 Takwimu za Sasa" else "📊 Live Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isSwahili) "Vyumba Vyote" else "Total Rooms",
                        value = "$totalRooms",
                        icon = Icons.Default.MeetingRoom,
                        iconTint = BlueAccent,
                        modifier = Modifier.weight(1f),
                        subtitle = if (isSwahili) "Uwezo wa $occupancyRate%" else "Occupancy: $occupancyRate%"
                    )
                    StatCard(
                        title = if (isSwahili) "Vilivyochukuliwa" else "Occupied",
                        value = "$occupiedRooms",
                        icon = Icons.Default.Lock,
                        iconTint = RedOccupied,
                        modifier = Modifier.weight(1f),
                        subtitle = if (isSwahili) "Wageni walio ndani" else "Current guests"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isSwahili) "Vilivyo Wazi" else "Vacant Rooms",
                        value = "$vacantRooms",
                        icon = Icons.Default.CheckCircle,
                        iconTint = GreenVacant,
                        modifier = Modifier.weight(1f),
                        subtitle = if (isSwahili) "Tayari kupokea mteja" else "Ready for guest"
                    )
                    StatCard(
                        title = if (isSwahili) "Chini ya Usafi" else "Cleaning",
                        value = "$cleaningRooms",
                        icon = Icons.Default.CleaningServices,
                        iconTint = OrangeCleaning,
                        modifier = Modifier.weight(1f),
                        subtitle = if (isSwahili) "Vinatayarishwa" else "In preparation"
                    )
                }

                // Today's Revenue Full Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = GoldSecondary.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GoldSecondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = GoldSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isSwahili) "Mapato ya Leo" else "Today's Revenue",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatMoney(todayRevenue),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = onNavigateToReports,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = GoldSecondary.copy(alpha = 0.25f),
                                contentColor = GoldSecondary
                            )
                        ) {
                            Text(if (isSwahili) "Ripoti" else "Report")
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = if (isSwahili) "⚡ Vitendo vya Haraka" else "⚡ Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToCheckIn,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_checkin_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSwahili) "Sajili Mteja" else "Check-In", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToRooms,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_room_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddHome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSwahili) "Vyumba" else "Rooms", fontSize = 13.sp)
                }
            }
        }

        // Quick Room Status Cards (Visual Quick Look)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSwahili) "🛏️ Muhtasari wa Vyumba" else "🛏️ Rooms Quick Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToRooms) {
                    Text(if (isSwahili) "Tazama Vyote" else "View All")
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(rooms) { room ->
                    RoomMiniCard(
                        room = room,
                        isSwahili = isSwahili,
                        onClick = onNavigateToRooms
                    )
                }
            }
        }

        // Active Guests Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSwahili) "👥 Wageni Waliopo Ndani (${activeBookings.size})" else "👥 In-House Guests (${activeBookings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToCheckIn) {
                    Text(if (isSwahili) "Orodha Kamili" else "Full List")
                }
            }
        }

        if (activeBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Hakuna mteja aliyepo ndani kwa sasa" else "No guests checked in right now",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToCheckIn,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isSwahili) "Sajili Mteja Mpya Sasa" else "Check In New Guest")
                        }
                    }
                }
            }
        } else {
            items(activeBookings) { booking ->
                ActiveGuestCard(
                    booking = booking,
                    isSwahili = isSwahili,
                    onCheckOutClick = { guestForCheckOut = booking }
                )
            }
        }
    }

    // Checkout Confirmation Dialog
    if (guestForCheckOut != null) {
        val booking = guestForCheckOut!!
        val balance = booking.totalAmount - booking.amountPaid

        AlertDialog(
            onDismissRequest = { guestForCheckOut = null },
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
                            "Je, una uhakika unataka kumfanyia Check-out mteja huyu?"
                        else
                            "Are you sure you want to check out this guest?"
                    )
                    Divider()
                    Text(text = "• ${if (isSwahili) "Mteja" else "Guest"}: ${booking.guestName}")
                    Text(text = "• ${if (isSwahili) "Chumba" else "Room"}: #${booking.roomNumber}")
                    Text(text = "• ${if (isSwahili) "Simu" else "Phone"}: ${booking.phoneNumber}")
                    Text(text = "• ${if (isSwahili) "Jumla" else "Total"}: ${formatMoney(booking.totalAmount)}")
                    Text(text = "• ${if (isSwahili) "Imelipwa" else "Paid"}: ${formatMoney(booking.amountPaid)}")
                    if (balance > 0) {
                        Text(
                            text = "⚠️ ${if (isSwahili) "Baki ya Kulipwa" else "Balance Due"}: ${formatMoney(balance)}",
                            color = RedOccupied,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "✅ ${if (isSwahili) "Amelipa Kamili" else "Fully Paid"}",
                            color = GreenVacant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSwahili)
                            "Hali ya chumba itawekwa kuwa 'Usafi' (Cleaning) ili kiandaliwe kwa mteja anayefuata."
                        else
                            "Room status will be set to 'Needs Cleaning' ready for the next guest.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.checkOutGuest(booking, RoomStatus.CLEANING) {
                            guestForCheckOut = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedOccupied)
                ) {
                    Text(if (isSwahili) "Kamilisha Check-out" else "Complete Check-out")
                }
            },
            dismissButton = {
                TextButton(onClick = { guestForCheckOut = null }) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun RoomMiniCard(
    room: RoomEntity,
    isSwahili: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${room.roomNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                StatusBadge(status = room.status, isSwahili = isSwahili)
            }
            RoomTypeBadge(type = room.roomType, isSwahili = isSwahili)
            Text(
                text = formatMoney(room.pricePerNight),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ActiveGuestCard(
    booking: GuestBookingEntity,
    isSwahili: Boolean,
    onCheckOutClick: () -> Unit
) {
    val balance = booking.totalAmount - booking.amountPaid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guest_card_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = booking.guestName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(
                            text = booking.guestName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "📞 ${booking.phoneNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${if (isSwahili) "Chumba" else "Room"} #${booking.roomNumber}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isSwahili) "Kuingia:" else "Check-in:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDate(booking.checkInDate),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = if (isSwahili) "Kutoka:" else "Check-out:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDate(booking.expectedCheckOutDate),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isSwahili) "Hali ya Malipo:" else "Payment:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (balance > 0) {
                        Text(
                            text = "${if (isSwahili) "Baki:" else "Due:"} ${formatMoney(balance)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedOccupied
                        )
                    } else {
                        Text(
                            text = if (isSwahili) "Imelipwa Yote" else "Paid in Full",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenVacant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onCheckOutClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RedOccupied
                    ),
                    modifier = Modifier.testTag("checkout_button_${booking.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSwahili) "Check-Out" else "Check-Out", fontSize = 13.sp)
                }
            }
        }
    }
}
