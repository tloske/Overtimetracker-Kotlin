package com.tloske.overtimetracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holiday-table")
data class HolidayData(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "start_date") val startDate: String = "",
    @ColumnInfo(name = "end_date") val endDate: String = "",
    @ColumnInfo(name = "days") val days: Int = 0
)
