package com.example.know_it_all

import android.app.Application
import androidx.room.Room
import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.data.repository.SwapRepository
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.util.SessionManager
import com.example.know_it_all.util.LocationService

class KnowItAllApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            KnowItAllDatabase::class.java,
            "know_it_all_db"
        ).fallbackToDestructiveMigration().build()
    }

    val sessionManager by lazy { SessionManager(this) }
    val locationService by lazy { LocationService(this) }

    val userRepository by lazy { UserRepository(database) }
    val swapRepository by lazy { SwapRepository(database) }
    val ledgerRepository by lazy { LedgerRepository(database) }
    val skillRepository by lazy { SkillRepository(database) }

    override fun onCreate() {
        super.onCreate()
    }
}
