package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomStatus
import com.example.data.model.RoomType
import com.example.ui.theme.GreenVacant
import com.example.ui.theme.OrangeCleaning
import com.example.ui.theme.PurpleSuite
import com.example.ui.theme.RedOccupied
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun formatMoney(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "TZS ${formatter.format(amount.toLong())}/="
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun StatusBadge(
    status: RoomStatus,
    isSwahili: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        RoomStatus.VACANT -> Triple(GreenVacant.copy(alpha = 0.15f), GreenVacant, if (isSwahili) "Wazi" else "Vacant")
        RoomStatus.OCCUPIED -> Triple(RedOccupied.copy(alpha = 0.15f), RedOccupied, if (isSwahili) "Kimechukuliwa" else "Occupied")
        RoomStatus.CLEANING -> Triple(OrangeCleaning.copy(alpha = 0.15f), OrangeCleaning, if (isSwahili) "Usafi" else "Cleaning")
        RoomStatus.MAINTENANCE -> Triple(Color.Gray.copy(alpha = 0.15f), Color.DarkGray, if (isSwahili) "Matengenezo" else "Maintenance")
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RoomTypeBadge(
    type: RoomType,
    isSwahili: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type) {
        RoomType.SINGLE -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        RoomType.DOUBLE -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
        RoomType.VIP -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        RoomType.SUITE -> Pair(PurpleSuite.copy(alpha = 0.15f), PurpleSuite)
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = bgColor
    ) {
        Text(
            text = if (isSwahili) type.displayNameSwahili.substringBefore(" (") else type.displayNameEnglish,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
