package com.tloske.overtimetracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holiday-table")
data class HolidayData(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "date") val date: Long = 0L,
    @ColumnInfo(name = "start_date") val startDate: Long = 0L,
    @ColumnInfo(name = "end_date") val endDate: Long = 0L,
    @ColumnInfo(name = "days") val days: Int = 0
)
