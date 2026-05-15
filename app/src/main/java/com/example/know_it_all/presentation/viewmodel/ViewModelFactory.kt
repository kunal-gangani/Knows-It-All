package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.know_it_all.data.repository.FirebaseLedgerRepository
import com.example.know_it_all.data.repository.FirebaseSkillRepository
import com.example.know_it_all.data.repository.FirebaseSwapRepository
import com.example.know_it_all.data.repository.FirebaseUserRepository
import com.example.know_it_all.util.SessionManager

class ViewModelFactory(
    private val userRepository: FirebaseUserRepository? = null,
    private val skillRepository: FirebaseSkillRepository? = null,
    private val swapRepository: FirebaseSwapRepository? = null,
    private val ledgerRepository: FirebaseLedgerRepository? = null,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    userRepository = requireNotNull(userRepository),
                    sessionManager = requireNotNull(sessionManager)
                ) as T
            }
            modelClass.isAssignableFrom(RadarViewModel::class.java) -> {
                RadarViewModel(
                    userRepository = requireNotNull(userRepository),
                    sessionManager = requireNotNull(sessionManager)
                ) as T
            }
            modelClass.isAssignableFrom(TradeViewModel::class.java) -> {
                TradeViewModel(
                    swapRepository = requireNotNull(swapRepository),
                    sessionManager = requireNotNull(sessionManager)
                ) as T
            }
            modelClass.isAssignableFrom(LedgerViewModel::class.java) -> {
                LedgerViewModel(
                    ledgerRepository = requireNotNull(ledgerRepository),
                    skillRepository  = requireNotNull(skillRepository),
                    userRepository   = requireNotNull(userRepository),
                    sessionManager   = requireNotNull(sessionManager)
                ) as T
            }
            modelClass.isAssignableFrom(SkillViewModel::class.java) -> {
                SkillViewModel(
                    skillRepository = requireNotNull(skillRepository),
                    sessionManager  = requireNotNull(sessionManager)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}