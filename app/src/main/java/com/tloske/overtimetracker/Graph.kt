package com.tloske.overtimetracker

import android.content.Context
import androidx.room.Room
import com.tloske.overtimetracker.data.HolidayRepository
import com.tloske.overtimetracker.data.OvertimeRepository

object Graph {
    private lateinit var database: AppDatabase

    val overtimeRepository by lazy {
        OvertimeRepository(overtimeDao = database.overtimeDao())
    }

    val holidayRepository by lazy {
        HolidayRepository(holidayDao = database.holidayDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration().build()
    }
}