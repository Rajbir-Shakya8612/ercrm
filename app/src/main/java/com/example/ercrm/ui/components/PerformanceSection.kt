package com.example.ercrm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ercrm.data.model.PerformanceData
import com.example.ercrm.data.model.PlansData
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun PerformanceSection(
    performanceData: PerformanceData,
    plansData: PlansData
) {
    val leadChartModelProducer = ChartEntryModelProducer(
        performanceData.leads.mapIndexed { index, value ->
            entryOf(index.toFloat(), value.toFloat())
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Performance",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Leads Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Leads Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Chart(
                    chart = lineChart(),
                    model = leadChartModelProducer.getModel()!!,
                    startAxis = startAxis(),
                    bottomAxis = bottomAxis(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        // Targets Progress
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Target Achievement",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TargetProgressItem(
                    title = "Monthly Target",
                    current = plansData.monthlyAchieved,
                    target = plansData.monthlyTarget,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TargetProgressItem(
                    title = "Quarterly Target",
                    current = plansData.quarterlyAchieved,
                    target = plansData.quarterlyTarget,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TargetProgressItem(
                    title = "Yearly Target",
                    current = plansData.yearlyAchieved,
                    target = plansData.yearlyTarget
                )
            }
        }
    }
}


@Composable
fun TargetProgressItem(
    title: String,
    current: Int,
    target: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (current.toFloat() / target.toFloat()) else 0f

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
