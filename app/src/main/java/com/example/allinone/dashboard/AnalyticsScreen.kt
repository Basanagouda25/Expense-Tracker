package com.example.allinone.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AnalyticsScreen() {
    val viewModel: ExpenseViewModel = viewModel()

    val monthlyTotals by viewModel.monthlyTotals.collectAsState()
    val weeklyTotals by viewModel.weeklyTotals.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()

    var selectedType by remember { mutableStateOf(AnalyticsType.MONTH) }

    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSurface = MaterialTheme.colorScheme.surface
    val themeBackground = MaterialTheme.colorScheme.background

    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBackground)
            .padding(20.dp)
    ) {
        // Title
        Text(
            text = "Analytics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Custom Toggle Switch (Month vs Week)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(themeSurface, RoundedCornerShape(25.dp))
                .padding(4.dp)
        ) {
            AnalyticsToggleButton(
                text = "Monthly",
                isSelected = selectedType == AnalyticsType.MONTH,
                selectedColor = themePrimary,
                modifier = Modifier.weight(1f)
            ) {
                selectedType = AnalyticsType.MONTH
            }
            AnalyticsToggleButton(
                text = "Weekly",
                isSelected = selectedType == AnalyticsType.WEEK,
                selectedColor = themePrimary,
                modifier = Modifier.weight(1f)
            ) {
                selectedType = AnalyticsType.WEEK
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chart Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Assuming MonthlyLineChart is a composable you've already created elsewhere
                if (selectedType == AnalyticsType.MONTH) {
                    MonthlyLineChart(monthlyTotals)
                } else {
                    MonthlyLineChart(weeklyTotals)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Category Breakdown",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Scrollable list of categories
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Convert Map to a List of Pairs to use in LazyColumn
            items(categoryTotals.toList()) { (category, total) ->
                CategoryBreakdownItem(
                    category = category,
                    total = total,
                    themeSurface = themeSurface,
                    themePrimary = themePrimary
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(category: String, total: Double, themeSurface: Color, themePrimary: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // A small colored indicator dot for the category
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(themePrimary, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "₹ $total",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Custom Toggle Button Composable (Similar to the Add Expense Screen)
@Composable
fun AnalyticsToggleButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) selectedColor else Color.Transparent
    val textColor = if (isSelected) Color.Black else Color.Gray

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

// Note: Ensure you have your MonthlyLineChart composable defined somewhere in your project!
// @Composable
// fun MonthlyLineChart(data: Map<String, Double>) { ... }