package com.example.mymoodtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyMood(
    @PrimaryKey val date: Long,
    @ColumnInfo(name = "value") val value: Int = 0,
    @ColumnInfo(name = "content") val content: String = "",
)
