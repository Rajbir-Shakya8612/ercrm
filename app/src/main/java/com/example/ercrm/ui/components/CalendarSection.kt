package com.example.ercrm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
//import com.example.ercrm.data.model.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

//@Composable
//fun CalendarSection(
//    selectedDate: LocalDate,
//    events: List<Event>,
//    onDateSelected: (LocalDate) -> Unit
//) {
//    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
//    val daysInMonth = currentMonth.lengthOfMonth()
//    val firstDayOfMonth = currentMonth.atDay(1)
//    val lastDayOfMonth = currentMonth.atDay(daysInMonth)
//    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
//    val days = (1..daysInMonth).toList()
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        // Month selector
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
//                Text("<")
//            }
//            Text(
//                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
//                style = MaterialTheme.typography.titleLarge
//            )
//            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
//                Text(">")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Days of week header
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            DayOfWeek.values().forEach { dayOfWeek ->
//                Text(
//                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Calendar grid
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(7),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            // Add empty cells for days before the first day of the month
//            items((1 until firstDayOfWeek).toList()) {
//                Box(modifier = Modifier.aspectRatio(1f))
//            }
//
//            // Add days of the month
//            items(days) { day ->
//                val date = currentMonth.atDay(day)
//                val isSelected = date == selectedDate
//                val hasEvent = events.any { event ->
//                    LocalDate.parse(event.start) == date
//                }
//
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(1f)
//                        .padding(4.dp)
//                        .clip(CircleShape)
//                        .background(
//                            when {
//                                isSelected -> MaterialTheme.colorScheme.primary
//                                hasEvent -> MaterialTheme.colorScheme.primaryContainer
//                                else -> Color.Transparent
//                            }
//                        )
//                        .border(
//                            width = 1.dp,
//                            color = if (hasEvent) MaterialTheme.colorScheme.primary else Color.Transparent,
//                            shape = CircleShape
//                        )
//                        .clickable { onDateSelected(date) },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = day.toString(),
//                        color = when {
//                            isSelected -> MaterialTheme.colorScheme.onPrimary
//                            hasEvent -> MaterialTheme.colorScheme.onPrimaryContainer
//                            else -> MaterialTheme.colorScheme.onSurface
//                        }
//                    )
//                }
//            }
//        }
//    }
//}