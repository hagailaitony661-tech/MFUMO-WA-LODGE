package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LodgeViewModel
import com.example.ui.screens.CheckInOutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RoomsScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen(
    val titleSwahili: String,
    val titleEnglish: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
) {
    DASHBOARD(
        titleSwahili = "Dashibodi",
        titleEnglish = "Dashboard",
        iconSelected = Icons.Filled.Dashboard,
        iconUnselected = Icons.Outlined.Dashboard
    ),
    ROOMS(
        titleSwahili = "Vyumba",
        titleEnglish = "Rooms",
        iconSelected = Icons.Filled.MeetingRoom,
        iconUnselected = Icons.Outlined.MeetingRoom
    ),
    CHECK_IN_OUT(
        titleSwahili = "Usajili",
        titleEnglish = "Check-In",
        iconSelected = Icons.Filled.PersonAdd,
        iconUnselected = Icons.Outlined.PersonAdd
    ),
    REPORTS(
        titleSwahili = "Ripoti",
        titleEnglish = "Reports",
        iconSelected = Icons.Filled.Assessment,
        iconUnselected = Icons.Outlined.Assessment
    )
}

class MainActivity : ComponentActivity() {
    private val viewModel: LodgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LodgeApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LodgeApp(viewModel: LodgeViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    val isSwahili by viewModel.isSwahili.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Handle system back button to navigate back to dashboard
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = Screen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hotel,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "LODGE MANAGER",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (isSwahili) "Mfumo wa Usimamizi wa Lodgi" else "Lodge Management System",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher Button (Kiswahili / English)
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.toggleLanguage() },
                        label = {
                            Text(
                                text = if (isSwahili) "🇹🇿 SWAHILI" else "🇬🇧 ENG",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("language_toggle_button")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                Screen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.iconSelected else screen.iconUnselected,
                                contentDescription = if (isSwahili) screen.titleSwahili else screen.titleEnglish
                            )
                        },
                        label = {
                            Text(
                                text = if (isSwahili) screen.titleSwahili else screen.titleEnglish,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToCheckIn = { currentScreen = Screen.CHECK_IN_OUT },
                    onNavigateToRooms = { currentScreen = Screen.ROOMS },
                    onNavigateToReports = { currentScreen = Screen.REPORTS }
                )
                Screen.ROOMS -> RoomsScreen(
                    viewModel = viewModel
                )
                Screen.CHECK_IN_OUT -> CheckInOutScreen(
                    viewModel = viewModel
                )
                Screen.REPORTS -> ReportsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
