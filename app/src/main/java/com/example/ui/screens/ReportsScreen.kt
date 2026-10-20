package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.data.model.GuestBookingEntity
import com.example.data.model.PaymentRecordEntity
import com.example.ui.LodgeViewModel
import com.example.ui.components.formatDateTime
import com.example.ui.components.formatMoney
import com.example.ui.theme.BlueAccent
import com.example.ui.components.SmallThermalReceiptDialog
import com.example.ui.components.BossInvoiceDialog
import com.example.ui.theme.GreenVacant
import com.example.ui.theme.RedOccupied

@Composable
fun ReportsScreen(
    viewModel: LodgeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSwahili by viewModel.isSwahili.collectAsState()
    val allBookings by viewModel.filteredAllBookings.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val searchQuery by viewModel.guestSearchQuery.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var receiptBooking by remember { mutableStateOf<GuestBookingEntity?>(null) }
    var invoiceBooking by remember { mutableStateOf<GuestBookingEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen")
    ) {
        // Top Revenue Summary Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isSwahili) "Jumla ya Mapato Yaliyokusanywa" else "Total Revenue Collected",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatMoney(totalRevenue),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalButton(
                            onClick = {
                                val csvText = buildString {
                                    appendLine("RIPOTI YA MAPATO YA LODGI - TOLEO LA EXCEL")
                                    appendLine("Na,Tarehe ya Malipo,Mteja,Chumba,Kiasi (TZS),Njia ya Malipo")
                                    allPayments.forEachIndexed { idx, p ->
                                        appendLine("${idx + 1},${formatDateTime(p.paymentDate)},\"${p.guestName}\",#${p.roomNumber},${p.amount.toLong()},${p.paymentMethod}")
                                    }
                                    appendLine(",,,JUMLA KUU,${totalRevenue.toLong()},")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csvText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Pakua / Shiriki Ripoti ya Excel"))
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excel (CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalIconButton(
                            onClick = {
                                val reportText = buildString {
                                    appendLine(if (isSwahili) "📋 RIPOTI YA LODGI" else "📋 LODGE REPORT")
                                    appendLine(if (isSwahili) "Mapato ya Leo: ${formatMoney(todayRevenue)}" else "Today's Revenue: ${formatMoney(todayRevenue)}")
                                    appendLine(if (isSwahili) "Jumla ya Mapato: ${formatMoney(totalRevenue)}" else "Total Revenue: ${formatMoney(totalRevenue)}")
                                    appendLine(if (isSwahili) "Idadi ya Wageni: ${allBookings.size}" else "Total Guests: ${allBookings.size}")
                                    appendLine(if (isSwahili) "Miamala ya Malipo: ${allPayments.size}" else "Total Transactions: ${allPayments.size}")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, if (isSwahili) "Tuma Ripoti" else "Share Report"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = if (isSwahili) "Tuma Ripoti" else "Share Report")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${if (isSwahili) "Mapato ya Leo:" else "Today's Revenue:"} ${formatMoney(todayRevenue)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${if (isSwahili) "Wageni wote:" else "Total Guests:"} ${allBookings.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Tabs: Payments vs Guest Records
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = if (isSwahili) "💵 Historia ya Malipo (${allPayments.size})" else "💵 Payments (${allPayments.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = if (isSwahili) "📜 Kumbukumbu za Wateja (${allBookings.size})" else "📜 Guest History (${allBookings.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (selectedTabIndex == 0) {
            // Payment Transactions List
            if (allPayments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSwahili) "Hakuna rekodi za malipo bado" else "No payment records found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allPayments) { payment ->
                        PaymentHistoryCard(payment = payment, isSwahili = isSwahili)
                    }
                }
            }
        } else {
            // Guest History List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.guestSearchQuery.value = it },
                    placeholder = {
                        Text(if (isSwahili) "Tafuta historia ya mteja (Jina, Simu, ID)..." else "Search history...")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.guestSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (allBookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSwahili) "Hakuna kumbukumbu za wateja zilizopatikana" else "No guest history found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allBookings) { booking ->
                            GuestHistoryCard(
                                booking = booking,
                                isSwahili = isSwahili,
                                onReceiptClick = { receiptBooking = booking },
                                onInvoiceClick = { invoiceBooking = booking }
                            )
                        }
                    }
                }
            }
        }
    }

    // Small Thermal Receipt Dialog
    if (receiptBooking != null) {
        SmallThermalReceiptDialog(
            booking = receiptBooking!!,
            onDismiss = { receiptBooking = null },
            onSwitchToBossInvoice = {
                val b = receiptBooking
                receiptBooking = null
                invoiceBooking = b
            }
        )
    }

    // Boss Invoice Dialog
    if (invoiceBooking != null) {
        BossInvoiceDialog(
            booking = invoiceBooking!!,
            onDismiss = { invoiceBooking = null },
            onSwitchToReceipt = {
                val b = invoiceBooking
                invoiceBooking = null
                receiptBooking = b
            }
        )
    }
}

@Composable
fun PaymentHistoryCard(
    payment: PaymentRecordEntity,
    isSwahili: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GreenVacant.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = GreenVacant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = payment.guestName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${if (isSwahili) "Chumba" else "Room"} #${payment.roomNumber} • ${payment.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDateTime(payment.paymentDate),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${formatMoney(payment.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GreenVacant
                )
                if (payment.notes.isNotBlank()) {
                    Text(
                        text = payment.notes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GuestHistoryCard(
    booking: GuestBookingEntity,
    isSwahili: Boolean,
    onReceiptClick: () -> Unit,
    onInvoiceClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.guestName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "📞 ${booking.phoneNumber} | ID: ${booking.idNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (booking.status == BookingStatus.ACTIVE)
                        RedOccupied.copy(alpha = 0.12f)
                    else
                        GreenVacant.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isSwahili) booking.status.swahili else booking.status.english,
                        color = if (booking.status == BookingStatus.ACTIVE) RedOccupied else GreenVacant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${if (isSwahili) "Chumba" else "Room"} #${booking.roomNumber} (${booking.daysCount} ${if (isSwahili) "siku" else "days"})",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${if (isSwahili) "Jumla" else "Total"}: ${formatMoney(booking.totalAmount)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onReceiptClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Risiti Ndogo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onInvoiceClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice ya Boss", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
