package com.example.ercrm.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.ercrm.R
import com.google.accompanist.pager.*

@Composable
fun NewDashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedBottom by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selected = selectedBottom) { selectedBottom = it }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Make content scrollable if needed
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 🔍 Search Bar
            SearchBar()

            Spacer(modifier = Modifier.height(20.dp))

            // 📱 Card Carousel
            HorizontalPager(
                count = 2,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) { index ->
                DashboardCard(index)
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = OrangePrimary,
                inactiveColor = Color(0xFFE9EEF6),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🧭 Tabs + Grid Content
            DashboardTabs(selectedTab) { selectedTab = it }
            Spacer(modifier = Modifier.height(18.dp))
            DashboardGrid()
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE9EEF6))
            .height(48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFB0B7C3), modifier = Modifier.padding(start = 18.dp))
        Text(
            text = "What would you like to do today?",
            color = Color(0xFFB0B7C3),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

@Composable
fun DashboardCard(index: Int) {
    val gradients = listOf(
        listOf(Color(0xFFFF6F00), Color(0xFFFFA726)),
        listOf(Color(0xFFFF6F00), Color(0xFFFFC371))
    )

    val gradient = gradients[index % gradients.size]

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(gradient))
            .padding(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("ICICI Bank", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("2300 1130 5224", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Account No.", color = Color.White, fontSize = 13.sp)
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Show Balance", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DashboardTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Home", "Shop", "Discover")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedTab == index) Color(0xFFFFF3E0) else Color.Transparent)
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == index) OrangePrimary else Color(0xFFB0B7C3),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardGrid() {
    val gridItems = listOf(
        Pair(Icons.Default.Fingerprint, "Attendance"),
        Pair(Icons.Default.Receipt, "Leads"),
        Pair(Icons.Default.BarChart, "Sales"),
        Pair(Icons.Default.AccountBalance, "Plans"),
        Pair(Icons.Default.Event, "Meetings"),
        Pair(Icons.Default.CheckCircle, "Tasks"),
        Pair(Icons.Default.AccountBalanceWallet, "Point Wallet"),
        Pair(Icons.Default.QrCode, "Primary Sales"),
        Pair(Icons.Default.ShoppingCart, "Secondary Sales")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(320.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(gridItems) { (icon, label) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = label, tint = OrangePrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, fontSize = 13.sp, color = Color(0xFF2E5AAC), fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            }
        }
    }
}


@Composable
fun BottomNavigationBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Pair(Icons.Default.Home, "Home"),
        Pair(Icons.Default.Star, "Favorites"),
        Pair(Icons.Default.QrCodeScanner, "Scan to Pay"),
        Pair(Icons.Default.LocalOffer, "Offers"),
        Pair(Icons.Default.Person, "Profile")
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, (icon, label) ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onSelect(index) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected == index) OrangePrimary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = label, tint = if (selected == index) Color.White else Color(0xFFB0B7C3))
                    }
                },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (selected == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected == index) OrangePrimary else Color(0xFFB0B7C3)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

private val OrangePrimary = Color(0xFFFF6F00)
