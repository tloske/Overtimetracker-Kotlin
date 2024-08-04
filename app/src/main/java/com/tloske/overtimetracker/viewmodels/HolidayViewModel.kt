package com.tloske.overtimetracker.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tloske.overtimetracker.Graph
import com.tloske.overtimetracker.data.HolidayData
import com.tloske.overtimetracker.data.HolidayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HolidayViewModel(private val holidayRepository: HolidayRepository = Graph.holidayRepository) :
    ViewModel() {
    var showBottomSheet by mutableStateOf(false)

    lateinit var getHolidayList: Flow<List<HolidayData>>

    init {
        viewModelScope.launch {
            getHolidayList = holidayRepository.getHolidayList()
        }
    }

    fun addHoliday(holidayData: HolidayData) {
        viewModelScope.launch(Dispatchers.IO) {
            holidayRepository.addHoliday(holidayData)
        }
        showBottomSheet = false
    }

    fun deleteHoliday(holidayData: HolidayData) {
        viewModelScope.launch(Dispatchers.IO) {
            holidayRepository.deleteHoliday(holidayData)
        }
    }
}