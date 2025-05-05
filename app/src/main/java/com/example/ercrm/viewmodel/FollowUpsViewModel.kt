package com.example.ercrm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.FollowUp
import com.example.ercrm.data.model.FollowUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowUpsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _followUps = MutableStateFlow<List<FollowUp>>(emptyList())
    val followUps: StateFlow<List<FollowUp>> = _followUps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchFollowUps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getFollowUps()
                if (response.isSuccessful && response.body()?.success == true) {
                    _followUps.value = response.body()?.followUps ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Failed to fetch follow-ups"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun scheduleFollowUp(leadId: Int, nextFollowUp: String, notes: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = FollowUpRequest(nextFollowUp, notes)
                val response = apiService.scheduleFollowUp(leadId, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                } else {
                    _error.value = response.body()?.message ?: "Failed to schedule follow-up"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 