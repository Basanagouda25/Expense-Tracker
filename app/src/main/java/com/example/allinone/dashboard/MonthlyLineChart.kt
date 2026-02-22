package com.example.allinone.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun MonthlyLineChart(
    data: Map<String, Double>
) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No Data Available", color = Color.Gray)
        }
        return
    }

    // Get your Compose theme colors and convert them to native Android colors
    val themePrimary = MaterialTheme.colorScheme.primary.toArgb()
    val themeSurface = MaterialTheme.colorScheme.surface.toArgb()
    val textAndAxisColor = android.graphics.Color.LTGRAY
    val gridColor = android.graphics.Color.DKGRAY

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            // FACTORY: Runs ONCE to initialize the chart styling
            LineChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false // Hide legend for a cleaner look

                // X-Axis Styling
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = textAndAxisColor
                xAxis.axisLineColor = textAndAxisColor
                xAxis.granularity = 1f

                // Y-Axis Styling
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = gridColor
                axisLeft.textColor = textAndAxisColor
                axisLeft.axisLineColor = textAndAxisColor

                setTouchEnabled(true)
                setPinchZoom(false)
                isDoubleTapToZoomEnabled = false
            }
        },
        update = { chart ->
            // UPDATE: Runs EVERY TIME the 'data' parameter changes (e.g., toggling Month/Week)
            val entries = ArrayList<Entry>()
            val labels = ArrayList<String>()

            data.entries.forEachIndexed { index, entry ->
                entries.add(Entry(index.toFloat(), entry.value.toFloat()))
                labels.add(entry.key)
            }

            val dataSet = LineDataSet(entries, "Spending").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER // Makes the line curved and smooth!
                color = themePrimary
                lineWidth = 3f

                // Dot styling
                setDrawCircles(true)
                setCircleColor(themePrimary)
                circleRadius = 5f
                circleHoleColor = themeSurface // Matches the card background

                // Values styling
                setDrawValues(true)
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 10f

                // Fill below the line (Optional but looks very premium)
                setDrawFilled(true)
                fillColor = themePrimary
                fillAlpha = 50 // Semi-transparent
            }

            // Update X-Axis labels
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            chart.data = LineData(dataSet)
            chart.animateY(800) // Animate every time data changes
            chart.invalidate() // Force a redraw
        }
    )
}