package com.example.ercrm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color

@Composable
fun OverviewSection(
    totalLeads: Int,
    monthlySales: Int,
    todayMeetings: Int,
    targetAchievement: Int
) {
    val metrics = listOf(
        MetricCardData(
            title = "Total Leads",
            value = totalLeads.toString(),
            icon = Icons.Default.People,
            color = MaterialTheme.colorScheme.primary
        ),
        MetricCardData(
            title = "Monthly Sales",
            value = "₹$monthlySales",
            icon = Icons.Default.AttachMoney,
            color = MaterialTheme.colorScheme.secondary
        ),
        MetricCardData(
            title = "Today's Meetings",
            value = todayMeetings.toString(),
            icon = Icons.Default.Event,
            color = MaterialTheme.colorScheme.tertiary
        ),
        MetricCardData(
            title = "Target Achievement",
            value = "$targetAchievement%",
            icon = Icons.Default.TrendingUp,
            color = MaterialTheme.colorScheme.primary
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(metrics) { metric ->
                MetricCard(metric)
            }
        }
    }
}

@Composable
fun MetricCard(metric: MetricCardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = metric.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = metric.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class MetricCardData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
) 