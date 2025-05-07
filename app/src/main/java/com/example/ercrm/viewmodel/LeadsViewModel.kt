package com.example.ercrm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class LeadsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val TAG = "LeadsViewModel"

    private val _leadsState = MutableStateFlow<LeadsResponse?>(null)
    val leadsState: StateFlow<LeadsResponse?> = _leadsState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchLeadDetails(leadId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Fetching details for lead: $leadId")
                val response = apiService.getLeadDetails(leadId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val leadResponse = response.body()
                    Log.d(TAG, "Successfully fetched lead details")
                    // Convert single lead response to LeadsResponse format
                    _leadsState.value = LeadsResponse(
                        lead_statuses = leadResponse?.leadStatuses ?: emptyList(),
                        leads = listOf(leadResponse?.lead ?: throw IllegalStateException("Lead not found"))
                    )
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to fetch lead details"
                    Log.e(TAG, "Error fetching lead details: $errorMessage")
                    _error.value = errorMessage
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while fetching lead details", e)
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody?.contains("Please mark your attendance") == true) {
                            _error.value = "Please mark your attendance before storing a lead"
                        } else {
                            _error.value = errorBody ?: "Failed to create lead"
                        }
                    } catch (e: Exception) {
                        _error.value = "Failed to create lead"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
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
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody?.contains("Please mark your attendance") == true) {
                            _error.value = "Please mark your attendance before storing a lead"
                        } else {
                            _error.value = errorBody ?: "Failed to update lead"
                        }
                    } catch (e: Exception) {
                        _error.value = "Failed to update lead"
                    }
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