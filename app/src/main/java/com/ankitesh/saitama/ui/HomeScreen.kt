package com.ankitesh.saitama.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ankitesh.saitama.ui.cig.CigViewModel
import com.ankitesh.saitama.ui.home.CigTile
import com.ankitesh.saitama.ui.home.WeightTile

@Composable
fun HomeScreen(
    weightViewModel: WeightViewModel,
    cigViewModel: CigViewModel,
    onNavigateToWeightDetail: () -> Unit,
    onNavigateToCigDetail: (openTargetDialog: Boolean) -> Unit
) {
    val averageWeight by weightViewModel.averageWeight.collectAsState()
    val todayWeight by weightViewModel.todayWeight.collectAsState()
    val lastRecordedWeight by weightViewModel.lastRecordedWeight.collectAsState()

    val todayCigCount by cigViewModel.todayCigCount.collectAsState()
    val cigTarget by cigViewModel.cigTarget.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weight Tile
        WeightTile(
            averageWeight = averageWeight,
            todayWeight = todayWeight,
            lastRecordedWeight = lastRecordedWeight,
            onWeightChange = { weight ->
                weightViewModel.saveTodayWeight(weight)
            },
            onNavigateToDetail = onNavigateToWeightDetail,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Cigarette Tile
        CigTile(
            todayCount = todayCigCount,
            target = cigTarget,
            onIncrement = { cigViewModel.incrementTodayCount() },
            onDecrement = { cigViewModel.decrementTodayCount() },
            onNavigateToDetail = { onNavigateToCigDetail(false) },
            onSetTarget = { onNavigateToCigDetail(true) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
