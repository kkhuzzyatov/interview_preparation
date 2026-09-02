package com.interviewpreparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeskViewModel : ViewModel() {

    private val _desks = MutableStateFlow<List<Desk>>(emptyList())
    val desks: StateFlow<List<Desk>> = _desks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDesks()
    }

    private fun loadDesks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = RetrofitClient.api.getDesks()
                _desks.value = response.desks

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load desks"
            } finally {
                _isLoading.value = false
            }
        }
    }
}