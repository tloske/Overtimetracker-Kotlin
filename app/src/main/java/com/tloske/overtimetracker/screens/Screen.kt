package com.tloske.overtimetracker.screens

sealed class Screen(val route: String) {
    data object OvertimeScreen : Screen("overtimescreen")
    data object HolidayScreen : Screen("holidayscreen")
}