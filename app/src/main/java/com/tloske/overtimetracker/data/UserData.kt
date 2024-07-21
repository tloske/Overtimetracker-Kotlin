package com.tloske.overtimetracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "holiday_amount") val holidayAmount: Int
)
