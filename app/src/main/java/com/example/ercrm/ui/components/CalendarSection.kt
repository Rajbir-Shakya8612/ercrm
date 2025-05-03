package com.example.ercrm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
//import com.example.ercrm.data.model.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val days = (1..daysInMonth).toList()
    val today = LocalDate.now()

    // Pastel color palette (repeat as needed)
    val pastelColors = listOf(
        Color(0xFFD0E8FF), // blue
        Color(0xFFDFF5D8), // green
        Color(0xFFFFF3C2), // yellow
        Color(0xFFFFD6D6), // pink
        Color(0xFFE6E6E6)  // gray
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("<")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(">")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DayOfWeek.values().forEach { dayOfWeek ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF0090FF), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(370.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Add empty cells for days before the first day of the month
            items((1 until firstDayOfWeek).toList()) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Add days of the month
            items(days) { day ->
                val date = currentMonth.atDay(day)
                val isSelected = date == selectedDate
                val isToday = date == today
                val color = pastelColors[(day - 1) % pastelColors.size]

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .border(
                            width = when {
                                isToday -> 3.dp
                                isSelected -> 2.dp
                                else -> 0.dp
                            },
                            color = when {
                                isToday -> Color(0xFF0090FF)
                                isSelected -> Color.DarkGray
                                else -> Color.Transparent
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = day.toString(),
                        color = Color(0xFF444444),
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                }
            }
        }
    }
}