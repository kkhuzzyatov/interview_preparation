package com.interviewpreparation.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.interviewpreparation.domain.model.DeskStatistics

@Composable
fun deskList(
    statistics: List<DeskStatistics>,
    onDeskClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "DESKS")

        LazyColumn {
            items(
                items = statistics,
                key = { it.deskId },
            ) { deskStatistics ->

                deskRow(
                    desk = deskStatistics,
                    onClick = {
                        onDeskClick(deskStatistics.deskId.toString())
                    },
                )

                HorizontalDivider()
            }
        }
    }
}
