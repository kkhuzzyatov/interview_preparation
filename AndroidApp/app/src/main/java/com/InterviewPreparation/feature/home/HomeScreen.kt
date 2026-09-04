package com.interviewpreparation.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.interviewpreparation.feature.home.components.deskList
import com.interviewpreparation.feature.home.components.reviewCard

@Composable
fun homeScreen(
    onStartReview: () -> Unit,
    onDeskClick: (String) -> Unit,
    viewModel: HomeViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        reviewCard(
            onStart = onStartReview,
        )

        Spacer(modifier = Modifier.height(28.dp))

        deskList(
            desks = state.desks,
            statistics = state.statistics,
            onDeskClick = onDeskClick,
        )
    }
}
