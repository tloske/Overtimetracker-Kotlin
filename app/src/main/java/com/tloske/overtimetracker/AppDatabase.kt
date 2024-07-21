package com.tloske.overtimetracker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tloske.overtimetracker.data.HolidayDao
import com.tloske.overtimetracker.data.HolidayData
import com.tloske.overtimetracker.data.OvertimeDao
import com.tloske.overtimetracker.data.OvertimeData

@Database(entities = [OvertimeData::class, HolidayData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holidayDao(): HolidayDao
    abstract fun overtimeDao(): OvertimeDao
}