package com.interviewpreparation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interviewpreparation.domain.model.Desk
import com.interviewpreparation.domain.model.DeskStatistics
import com.interviewpreparation.domain.repository.DeskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val desks: List<Desk> = emptyList(),
    val statistics: List<DeskStatistics> = emptyList(),
    val error: String? = null,
)

class HomeViewModel(
    private val repository: DeskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            try {
                val desks = repository.getDesks()
                val statistics = repository.getStatistics()

                _uiState.value =
                    HomeUiState(
                        isLoading = false,
                        desks = desks,
                        statistics = statistics,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    HomeUiState(
                        isLoading = false,
                        error = e.message ?: "Unable to load desks",
                    )
            }
        }
    }
}
