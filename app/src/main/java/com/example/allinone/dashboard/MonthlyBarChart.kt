package com.example.allinone.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthlyBarChart(
    data: Map<String, Double>
) {

    if (data.isEmpty()) {
        Text("No Data Available")
        return
    }

    val maxAmount = data.values.maxOrNull() ?: 1.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text("Monthly Trend", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            data.forEach { (month, amount) ->

                val barHeightRatio = (amount / maxAmount).toFloat()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .fillMaxHeight(barHeightRatio)
                            .background(
                                Color(0xFF4CAF50),
                                RoundedCornerShape(6.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = month.take(3),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}