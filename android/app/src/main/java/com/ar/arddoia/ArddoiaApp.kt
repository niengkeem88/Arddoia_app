package com.ar.arddoia

import android.app.Application
import com.ar.arddoia.data.local.ArddoiaDatabase
import com.ar.arddoia.data.repository.HabitRepository

class ArddoiaApp : Application() {
    val database: ArddoiaDatabase by lazy { ArddoiaDatabase.getInstance(this) }
    val repository: HabitRepository by lazy { HabitRepository(database.habitDao()) }
}
