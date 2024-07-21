package com.tloske.overtimetracker

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}