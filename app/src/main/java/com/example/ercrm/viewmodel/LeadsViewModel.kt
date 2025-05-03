package com.example.ercrm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _leadsState = MutableStateFlow<LeadsResponse?>(null)
    val leadsState: StateFlow<LeadsResponse?> = _leadsState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchLeads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getLeads()
                if (response.isSuccessful) {
                    _leadsState.value = response.body()
                } else {
                    _error.value = "Failed to load leads"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun createLead(lead: LeadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.createLead(lead)
                if (response.isSuccessful) {
                    fetchLeads()
                    onSuccess()
                } else {
                    _error.value = "Failed to create lead"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun deleteLead(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteLead(id)
                if (response.isSuccessful) {
                    fetchLeads()
                } else {
                    _error.value = "Failed to delete lead"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun updateLead(id: Int, lead: LeadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updateLead(id, lead)
                if (response.isSuccessful) {
                    fetchLeads()
                    onSuccess()
                } else {
                    _error.value = "Failed to update lead"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
} 