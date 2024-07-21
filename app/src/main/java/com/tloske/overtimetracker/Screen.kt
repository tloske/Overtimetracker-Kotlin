package com.tloske.overtimetracker

import androidx.annotation.StringRes

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    data object Overtime : Screen("overtime", R.string.overtime)
    data object Holiday : Screen("holiday", R.string.holiday)
}