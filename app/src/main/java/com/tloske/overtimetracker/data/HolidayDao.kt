package com.tloske.overtimetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HolidayDao {
    @Query("SELECT * FROM `holiday-table`")
    abstract fun getAll(): Flow<List<HolidayData>>

    @Query("SELECT * FROM `holiday-table` WHERE uid=:uid")
    abstract fun getById(uid: Int): HolidayData

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(holidayData: HolidayData)

    @Delete
    abstract fun delte(holidayData: HolidayData)
}