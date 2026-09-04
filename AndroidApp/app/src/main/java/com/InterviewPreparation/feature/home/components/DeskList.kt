package com.interviewpreparation.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.interviewpreparation.domain.model.Desk
import com.interviewpreparation.domain.model.DeskStatistics

@Composable
fun deskList(
    desks: List<Desk>,
    statistics: List<DeskStatistics>,
    onDeskClick: (String) -> Unit,
) {
    val statisticsByDeskId = statistics.associateBy { it.deskId }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "DESKS")

        LazyColumn {
            items(
                items = desks,
                key = { it.id },
            ) { desk ->
                val count = statisticsByDeskId[desk.id]?.total ?: 0

                deskRow(
                    name = desk.name,
                    count = count,
                    onClick = {
                        onDeskClick(desk.id.toString())
                    },
                )

                HorizontalDivider()
            }
        }
    }
}
