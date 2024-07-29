package com.tloske.overtimetracker.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tloske.overtimetracker.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val icon: Int) {
    data object Overtime : Screen("overtime", R.string.overtime, R.drawable.baseline_access_time_24)
    data object Holiday : Screen("holiday", R.string.holiday, R.drawable.baseline_beach_access_24)
}