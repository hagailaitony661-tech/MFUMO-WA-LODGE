package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GuestBookingEntity
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.GreenVacant
import com.example.ui.theme.RedOccupied

/**
 * RISITI NDOGO YA MASHINE YA POS / TRA (Thermal Slip Style - 80mm)
 */
@Composable
fun SmallThermalReceiptDialog(
    booking: GuestBookingEntity,
    lodgeName: String = "LODGE & SUITES MANAGEMENT",
    lodgePhone: String = "0754 000 000",
    lodgeTin: String = "100-000-000",
    lodgeAddress: String = "Mtaa wa Posta, Mkoani, TZ",
    onDismiss: () -> Unit,
    onSwitchToBossInvoice: () -> Unit
) {
    val context = LocalContext.current
    val balance = booking.totalAmount - booking.amountPaid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = GreenVacant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Risiti Ndogo ya Mashine (80mm)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Funga")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Themed Thermal Receipt Paper Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                    color = Color(0xFFFAFAFA),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header info
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Hotel,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = lodgeName.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = lodgeAddress,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Simu: $lodgePhone | TIN: $lodgeTin",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "*** RISITI YA FEDHA (EFD/POS) ***",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Meta details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "RISITI NA:", fontSize = 11.sp, color = Color.DarkGray)
                            Text(
                                text = "#REC-${booking.id.toString().takeLast(4)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "TAREHE:", fontSize = 11.sp, color = Color.DarkGray)
                            Text(text = formatDateTime(booking.checkInDate), fontSize = 11.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "MHUDUMU:", fontSize = 11.sp, color = Color.DarkGray)
                            Text(text = "Mapokezi", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Guest info
                        Text(
                            text = "MTEJA: ${booking.guestName.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "SIMU: ${booking.phoneNumber}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "KITAMBULISHO / NIDA: ${booking.idNumber}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Room & Charges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CHUMBA #${booking.roomNumber}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${booking.daysCount} Siku",
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "JUMLA:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = formatMoney(booking.totalAmount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "IMELIPWA:", fontWeight = FontWeight.Bold, color = GreenVacant, fontSize = 12.sp)
                            Text(
                                text = formatMoney(booking.amountPaid),
                                fontWeight = FontWeight.Bold,
                                color = GreenVacant,
                                fontSize = 12.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "BAKI:",
                                fontWeight = FontWeight.Bold,
                                color = if (balance > 0) RedOccupied else Color.Gray,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formatMoney(balance),
                                fontWeight = FontWeight.Bold,
                                color = if (balance > 0) RedOccupied else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "NJIA:", fontSize = 11.sp, color = Color.DarkGray)
                            Text(text = booking.paymentMethod, fontWeight = FontWeight.Medium, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ASANTE KWA KUTUCHAGUA!",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "KARIBU TENA",
                                fontSize = 10.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = " TRA VERIFIED RECEIPT ",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            shareText(
                                context = context,
                                title = "Risiti ya Malipo",
                                text = buildReceiptShareText(booking, lodgeName, lodgePhone)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenVacant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tuma Risiti", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSwitchToBossInvoice,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Invoice ya Boss", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * INVOICE YA BOSS (EXECUTIVE STATEMENT OF CHARGES & TAX INVOICE)
 */
@Composable
fun BossInvoiceDialog(
    booking: GuestBookingEntity,
    lodgeName: String = "LODGE & SUITES MANAGEMENT",
    lodgePhone: String = "0754 000 000",
    lodgeEmail: String = "info@lodgemanager.co.tz",
    lodgeTin: String = "100-000-000",
    lodgeAddress: String = "Mtaa wa Posta, Mkoani, Tanzania",
    onDismiss: () -> Unit,
    onSwitchToReceipt: () -> Unit
) {
    val context = LocalContext.current
    val balance = booking.totalAmount - booking.amountPaid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BlueAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "HATI RASMI YA MALIPO (INVOICE)",
                            color = BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Funga")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official Invoice Sheet Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Brand and Invoice Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1E3A8A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Hotel,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = lodgeName.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = lodgeAddress,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Simu: $lodgePhone",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "#INV-${booking.id.toString().takeLast(4)}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                Text(
                                    text = formatDate(booking.checkInDate),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = if (balance <= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (balance <= 0) "IMELIPWA" else "INA BAKI",
                                        color = if (balance <= 0) Color(0xFF15803D) else Color(0xFFB91C1C),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Billed To Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "BILLED TO / TAARIFA ZA MTEJA:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = booking.guestName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Simu: ${booking.phoneNumber} • NIDA/ID: ${booking.idNumber}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Breakdown Table
                        Text(
                            text = "MAELEZO YA HUDUMA:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Huduma", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Muda", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Jumla", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Malazi ya Chumba #${booking.roomNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Huduma kamili ya chumbani",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "${booking.daysCount} Usiku",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Text(
                                text = formatMoney(booking.totalAmount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Totals Summary
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier.widthIn(min = 200.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Gharama Halisi:", fontSize = 11.sp, color = Color.Gray)
                                Text(text = formatMoney(booking.totalAmount), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(
                                modifier = Modifier.widthIn(min = 200.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "VAT (18%):", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "Imejumuishwa", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.widthIn(min = 200.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Jumla Kamili:", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                Text(
                                    text = formatMoney(booking.totalAmount),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                            Row(
                                modifier = Modifier.widthIn(min = 200.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Kiasi Kilicholipwa:", fontWeight = FontWeight.Bold, color = GreenVacant, fontSize = 11.sp)
                                Text(
                                    text = formatMoney(booking.amountPaid),
                                    fontWeight = FontWeight.Bold,
                                    color = GreenVacant,
                                    fontSize = 11.sp
                                )
                            }
                            if (balance > 0) {
                                Row(
                                    modifier = Modifier.widthIn(min = 200.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Baki Inayodaiwa:", fontWeight = FontWeight.Bold, color = RedOccupied, fontSize = 11.sp)
                                    Text(
                                        text = formatMoney(balance),
                                        fontWeight = FontWeight.Bold,
                                        color = RedOccupied,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Signature and Stamp Area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Mapokezi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Divider(modifier = Modifier.width(90.dp), color = Color.DarkGray)
                                Text(
                                    text = "Sahihi ya Mpokeaji",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Meneja / Boss",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Divider(modifier = Modifier.width(90.dp), color = Color.DarkGray)
                                Text(
                                    text = "Muhuri na Sahihi",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            shareText(
                                context = context,
                                title = "Invoice ya Boss",
                                text = buildInvoiceShareText(booking, lodgeName, lodgePhone, balance)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tuma Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSwitchToReceipt,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Risiti Ndogo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun shareText(context: Context, title: String, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, title))
}

private fun buildReceiptShareText(b: GuestBookingEntity, lodge: String, phone: String): String {
    return buildString {
        appendLine("*$lodge*")
        appendLine("Simu: $phone")
        appendLine("------------------------------")
        appendLine("*RISITI YA MALIPO YA LODGI (80MM)*")
        appendLine("Risiti No: #REC-${b.id.toString().takeLast(4)}")
        appendLine("Tarehe: ${formatDateTime(b.checkInDate)}")
        appendLine("Mteja: ${b.guestName.uppercase()}")
        appendLine("Simu: ${b.phoneNumber}")
        appendLine("Kitambulisho: ${b.idNumber}")
        appendLine("Chumba: #${b.roomNumber} (${b.daysCount} Siku)")
        appendLine("Jumla: ${formatMoney(b.totalAmount)}")
        appendLine("Imelipwa: ${formatMoney(b.amountPaid)} (${b.paymentMethod})")
        val bal = b.totalAmount - b.amountPaid
        appendLine("Baki: ${formatMoney(bal)}")
        appendLine("------------------------------")
        appendLine("Asante kwa kutuchagua! Karibu tena.")
    }
}

private fun buildInvoiceShareText(b: GuestBookingEntity, lodge: String, phone: String, bal: Double): String {
    return buildString {
        appendLine("*INVOICE YA MALIPO YA LODGI (TAX INVOICE)*")
        appendLine("==============================")
        appendLine("Ofisi: $lodge")
        appendLine("Simu: $phone")
        appendLine("Invoice No: #INV-${b.id.toString().takeLast(4)}")
        appendLine("Tarehe: ${formatDate(b.checkInDate)}")
        appendLine("------------------------------")
        appendLine("Billed To: ${b.guestName}")
        appendLine("Simu: ${b.phoneNumber}")
        appendLine("Chumba: #${b.roomNumber} (${b.daysCount} Usiku)")
        appendLine("Gharama Kamili: ${formatMoney(b.totalAmount)}")
        appendLine("Kiasi Kilicholipwa: ${formatMoney(b.amountPaid)}")
        appendLine("Baki Inayodaiwa: ${formatMoney(bal)}")
        appendLine("Hali: ${if (bal <= 0) "IMELIPWA YOTE" else "INA BAKI"}")
        appendLine("==============================")
        appendLine("Asante sana kwa ushirikiano wako!")
    }
}
