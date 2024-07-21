package com.tloske.overtimetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OvertimeDao {
    @Query("SELECT * FROM `overtime-table`")
    abstract fun getAll(): Flow<List<OvertimeData>>
    
    @Query("SELECT * FROM `overtime-table` WHERE uid=:uid")
    abstract fun getById(uid: Int): OvertimeData

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(overtimeData: OvertimeData)

    @Delete
    abstract fun delete(overtimeData: OvertimeData)
}