package com.elderease.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.elderease.app.data.HeartRateDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BloodPressureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BloodPressureRepository

    val allRecords: StateFlow<List<BloodPressureRecord>>

    init {
        val bloodPressureDao = HeartRateDatabase.Companion.getDatabase(application).bloodPressureDao()
        repository = BloodPressureRepository(bloodPressureDao)
        allRecords = repository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insert(record: BloodPressureRecord) = viewModelScope.launch {
        repository.insert(record)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return BloodPressureViewModel(application) as T
            }
        }
    }
}