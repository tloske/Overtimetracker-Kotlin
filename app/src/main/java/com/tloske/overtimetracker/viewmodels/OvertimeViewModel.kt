package com.tloske.overtimetracker.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tloske.overtimetracker.Graph
import com.tloske.overtimetracker.data.OvertimeData
import com.tloske.overtimetracker.data.OvertimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OvertimeViewModel(private val overtimeRepository: OvertimeRepository = Graph.overtimeRepository) :
    ViewModel() {
    var showBottomSheet by mutableStateOf(false)

    lateinit var getOvertimeList: Flow<List<OvertimeData>>

    init {
        viewModelScope.launch {
            getOvertimeList = overtimeRepository.getOvertimeList()
        }
    }

    fun addOvertime(overtimeData: OvertimeData) {
        viewModelScope.launch(Dispatchers.IO) {
            overtimeRepository.addOvertime(overtimeData)
        }
        showBottomSheet = false
    }

    fun deleteOvertime(overtimeData: OvertimeData) {
        viewModelScope.launch(Dispatchers.IO) {
            overtimeRepository.deleteOvertime(overtimeData)
        }
    }
}