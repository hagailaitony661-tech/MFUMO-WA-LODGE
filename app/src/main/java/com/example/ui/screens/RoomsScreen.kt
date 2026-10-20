package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import com.example.data.model.RoomType
import com.example.ui.LodgeViewModel
import com.example.ui.components.RoomTypeBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatMoney
import com.example.ui.theme.RedOccupied

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    viewModel: LodgeViewModel,
    modifier: Modifier = Modifier
) {
    val isSwahili by viewModel.isSwahili.collectAsState()
    val rooms by viewModel.filteredRooms.collectAsState()
    val searchQuery by viewModel.roomSearchQuery.collectAsState()
    val filterStatus by viewModel.roomFilterStatus.collectAsState()
    val filterType by viewModel.roomFilterType.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRoomForActions by remember { mutableStateOf<RoomEntity?>(null) }
    var roomToDelete by remember { mutableStateOf<RoomEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_room_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = if (isSwahili) "Ongeza Chumba" else "Add Room")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("rooms_screen")
        ) {
            // Header & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.roomSearchQuery.value = it },
                    placeholder = {
                        Text(if (isSwahili) "Tafuta chumba (namba, ghorofa, vifaa)..." else "Search room (number, floor)...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.roomSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_rooms_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = filterStatus == null,
                            onClick = { viewModel.roomFilterStatus.value = null },
                            label = { Text(if (isSwahili) "Vyote" else "All") }
                        )
                    }
                    items(RoomStatus.values()) { status ->
                        FilterChip(
                            selected = filterStatus == status,
                            onClick = {
                                viewModel.roomFilterStatus.value = if (filterStatus == status) null else status
                            },
                            label = { Text(if (isSwahili) status.swahili else status.english) }
                        )
                    }
                }
            }

            // Room list
            if (rooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isSwahili) "Hakuna chumba kilichopatikana" else "No rooms found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSwahili) "Ongeza Chumba Kipya" else "Add New Room")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${if (isSwahili) "Jumla ya vyumba:" else "Total rooms:"} ${rooms.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(rooms) { room ->
                        RoomDetailCard(
                            room = room,
                            isSwahili = isSwahili,
                            onStatusClick = { selectedRoomForActions = room },
                            onDeleteClick = { roomToDelete = room }
                        )
                    }
                }
            }
        }
    }

    // Add Room Dialog
    if (showAddDialog) {
        AddRoomDialog(
            isSwahili = isSwahili,
            onDismiss = { showAddDialog = false },
            onConfirm = { number, type, price, floor, amenities ->
                viewModel.addNewRoom(number, type, price, floor, amenities)
                showAddDialog = false
            }
        )
    }

    // Change Status Dialog
    if (selectedRoomForActions != null) {
        val room = selectedRoomForActions!!
        RoomStatusChangeDialog(
            room = room,
            isSwahili = isSwahili,
            onDismiss = { selectedRoomForActions = null },
            onSelectStatus = { newStatus ->
                viewModel.updateRoomStatus(room.id, newStatus)
                selectedRoomForActions = null
            }
        )
    }

    // Delete Confirmation
    if (roomToDelete != null) {
        val room = roomToDelete!!
        AlertDialog(
            onDismissRequest = { roomToDelete = null },
            title = { Text(if (isSwahili) "Futa Chumba" else "Delete Room") },
            text = {
                Text(
                    if (isSwahili)
                        "Je, una uhakika unataka kufuta Chumba #${room.roomNumber}?"
                    else
                        "Are you sure you want to delete Room #${room.roomNumber}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRoom(room)
                        roomToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedOccupied)
                ) {
                    Text(if (isSwahili) "Futa" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { roomToDelete = null }) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun RoomDetailCard(
    room: RoomEntity,
    isSwahili: Boolean,
    onStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("room_item_${room.roomNumber}"),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = room.roomNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column {
                        Text(
                            text = "${if (isSwahili) "Chumba" else "Room"} #${room.roomNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = room.floor,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(status = room.status, isSwahili = isSwahili)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoomTypeBadge(type = room.roomType, isSwahili = isSwahili)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isSwahili) "Bei kwa siku:" else "Daily rate:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMoney(room.pricePerNight),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (room.amenities.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = room.amenities,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onStatusClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSwahili) "Badili Hali" else "Change Status", fontSize = 13.sp)
                }

                if (room.status != RoomStatus.OCCUPIED) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = if (isSwahili) "Futa" else "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomDialog(
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (number: String, type: RoomType, price: Double, floor: String, amenities: String) -> Unit
) {
    var roomNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RoomType.SINGLE) }
    var priceText by remember { mutableStateOf("35000") }
    var floorText by remember { mutableStateOf("Ghorofa ya 1") }
    var amenitiesText by remember { mutableStateOf("WiFi, AC, TV, Choo cha Ndani") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isSwahili) "Ongeza Chumba Kipya" else "Add New Room",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text(if (isSwahili) "Namba ya Chumba (mf. 104, 203, VIP 1)*" else "Room Number*") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_room_number")
                )

                Text(
                    text = if (isSwahili) "Aina ya Chumba:" else "Room Type:",
                    style = MaterialTheme.typography.labelMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(RoomType.values()) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (type == RoomType.SINGLE) priceText = "35000"
                                if (type == RoomType.DOUBLE) priceText = "60000"
                                if (type == RoomType.VIP) priceText = "120000"
                                if (type == RoomType.SUITE) priceText = "180000"
                            },
                            label = { Text(if (isSwahili) type.displayNameSwahili.substringBefore(" (") else type.displayNameEnglish, fontSize = 12.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(if (isSwahili) "Bei kwa Siku (TZS)*" else "Rate per Day (TZS)*") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_room_price")
                )

                OutlinedTextField(
                    value = floorText,
                    onValueChange = { floorText = it },
                    label = { Text(if (isSwahili) "Sakafu / Ghorofa" else "Floor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amenitiesText,
                    onValueChange = { amenitiesText = it },
                    label = { Text(if (isSwahili) "Vifaa na Huduma" else "Amenities") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 35000.0
                    onConfirm(roomNumber, selectedType, price, floorText, amenitiesText)
                },
                modifier = Modifier.testTag("confirm_add_room_button")
            ) {
                Text(if (isSwahili) "Hifadhi Chumba" else "Save Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Ghairi" else "Cancel")
            }
        }
    )
}

@Composable
fun RoomStatusChangeDialog(
    room: RoomEntity,
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onSelectStatus: (RoomStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${if (isSwahili) "Badili Hali ya Chumba" else "Change Status for Room"} #${room.roomNumber}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isSwahili) "Chagua hali mpya:" else "Select new status:",
                    style = MaterialTheme.typography.bodyMedium
                )
                RoomStatus.values().forEach { status ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { onSelectStatus(status) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (room.status == status)
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isSwahili) status.swahili else status.english,
                                fontWeight = FontWeight.SemiBold
                            )
                            StatusBadge(status = status, isSwahili = isSwahili)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSwahili) "Funga" else "Close")
            }
        }
    )
}
