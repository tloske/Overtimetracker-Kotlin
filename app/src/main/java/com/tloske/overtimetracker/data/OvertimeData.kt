package com.tloske.overtimetracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "overtime-table")
data class OvertimeData(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0L,
    @ColumnInfo(name = "date") val date: Long = 0L,
    @ColumnInfo(name = "starttime") val startTime: String = "",
    @ColumnInfo(name = "endtime") val endTime: String = "",
    @ColumnInfo(name = "hours") val hours: Float = 0f
)
