package com.tloske.overtimetracker.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface HolidayDao {
    @Query("SELECT * FROM `holiday-table`")
    fun getAll(): List<HolidayData>
}