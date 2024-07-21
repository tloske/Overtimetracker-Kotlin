package com.tloske.overtimetracker.data

import kotlinx.coroutines.flow.Flow

class OvertimeRepository(private val overtimeDao: OvertimeDao) {
    suspend fun addOvertime(overtimeData: OvertimeData) = overtimeDao.insert(overtimeData)

    fun getOvertimeList(): Flow<List<OvertimeData>> = overtimeDao.getAll()

    suspend fun deleteOvertime(overtimeData: OvertimeData) = overtimeDao.delete(overtimeData)
}