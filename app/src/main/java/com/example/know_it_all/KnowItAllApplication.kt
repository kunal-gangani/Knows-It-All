package com.example.know_it_all

import android.app.Application
import com.example.know_it_all.data.repository.FirebaseLedgerRepository
import com.example.know_it_all.data.repository.FirebaseSkillRepository
import com.example.know_it_all.data.repository.FirebaseSwapRepository
import com.example.know_it_all.data.repository.FirebaseUserRepository
import com.example.know_it_all.util.SessionManager
import com.google.firebase.FirebaseApp

class KnowItAllApplication : Application() {

    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    val userRepository: FirebaseUserRepository by lazy {
        FirebaseUserRepository()
    }

    val skillRepository: FirebaseSkillRepository by lazy {
        FirebaseSkillRepository()
    }

    val swapRepository: FirebaseSwapRepository by lazy {
        FirebaseSwapRepository()
    }

    val ledgerRepository: FirebaseLedgerRepository by lazy {
        FirebaseLedgerRepository()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}