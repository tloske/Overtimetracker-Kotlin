package com.tloske.overtimetracker.data

import kotlinx.coroutines.flow.Flow

class HolidayRepository(private val holidayDao: HolidayDao) {
    suspend fun addHoliday(holidayData: HolidayData) = holidayDao.insert(holidayData)

    fun getHolidayList(): Flow<List<HolidayData>> = holidayDao.getAll()

    suspend fun deleteHoliday(holidayData: HolidayData) = holidayDao.delte(holidayData)
}