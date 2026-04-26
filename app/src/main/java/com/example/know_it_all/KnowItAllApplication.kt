package com.example.know_it_all

import android.app.Application
import com.example.know_it_all.data.local.db.KnowItAllDatabase
import com.example.know_it_all.data.remote.RetrofitClient
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.data.repository.SwapRepository
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.SessionManager

/**
 * Fixes applied:
 *
 *  Repositories were previously constructed as:
 *    UserRepository(database)
 *    SwapRepository(database)
 *    LedgerRepository(database)
 *    SkillRepository(database)
 *
 *  After the Batch 3 refactor every repository now takes injected DAOs
 *  and services instead of the KnowItAllDatabase god-object:
 *    UserRepository(userDao, userService)
 *    SwapRepository(swapDao, swapService)
 *    LedgerRepository(ledgerDao, ledgerService)
 *    SkillRepository(skillDao, skillService)
 *
 *  This file is the single construction site — all DAOs and services are
 *  resolved here and passed into repositories. ViewModels receive
 *  repositories via ViewModelFactory; they never touch the database or
 *  RetrofitClient directly.
 *
 *  Migration note: once Hilt is added, delete the manual construction below
 *  and replace with @HiltAndroidApp + @Module/@Provides bindings.
 */
class KnowItAllApplication : Application() {

    // Database — single instance for the entire app lifetime
    val database: KnowItAllDatabase by lazy {
        KnowItAllDatabase.getInstance(this)
    }

    // Session
    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    // Repositories — each receives its specific DAOs and services
    val userRepository: UserRepository by lazy {
        UserRepository(
            userDao = database.userDao(),
            userService = RetrofitClient.userService
        )
    }

    val swapRepository: SwapRepository by lazy {
        SwapRepository(
            swapDao = database.swapDao(),
            swapService = RetrofitClient.swapService
        )
    }

    val ledgerRepository: LedgerRepository by lazy {
        LedgerRepository(
            ledgerDao = database.trustLedgerDao(),
            ledgerService = RetrofitClient.ledgerService
        )
    }

    val skillRepository: SkillRepository by lazy {
        SkillRepository(
            skillDao = database.skillDao(),
            skillService = RetrofitClient.skillService
        )
    }
}