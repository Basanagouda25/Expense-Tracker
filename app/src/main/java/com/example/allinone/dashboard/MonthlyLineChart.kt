package com.example.allinone.dashboard


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun MonthlyLineChart(
    data: Map<String, Double>
) {

    if (data.isEmpty()) {
        Text("No Monthly Data")
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),

        factory = { context ->

            val chart = LineChart(context)

            val entries = ArrayList<Entry>()
            val labels = ArrayList<String>()

            data.entries.forEachIndexed { index, entry ->
                entries.add(
                    Entry(index.toFloat(), entry.value.toFloat())
                )
                labels.add(entry.key)
            }

            val dataSet = LineDataSet(entries, "Monthly Spending")

            dataSet.valueTextColor = android.graphics.Color.WHITE
            dataSet.color = android.graphics.Color.CYAN
            dataSet.setCircleColor(android.graphics.Color.YELLOW)
            dataSet.lineWidth = 3f
            dataSet.circleRadius = 6f
            dataSet.valueTextSize = 12f
            dataSet.setDrawValues(true)

            val lineData = LineData(dataSet)
            chart.data = lineData

            // Remove description
            chart.description.isEnabled = false

            // Remove right axis
            chart.axisRight.isEnabled = false

            // X Axis
            val xAxis = chart.xAxis
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter =
                com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)

            // Y Axis
            chart.axisLeft.setDrawGridLines(true)
            chart.xAxis.textColor = android.graphics.Color.WHITE
            chart.axisLeft.textColor = android.graphics.Color.WHITE

            chart.setTouchEnabled(true)
            chart.setPinchZoom(true)

            chart.animateY(1000)

            chart.invalidate()

            chart
        }
    )
}