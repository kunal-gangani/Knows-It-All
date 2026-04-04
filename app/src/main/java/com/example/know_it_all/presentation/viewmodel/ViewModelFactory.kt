package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.data.repository.SwapRepository
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.util.SessionManager

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val swapRepository: SwapRepository? = null,
    private val ledgerRepository: LedgerRepository? = null,
    private val skillRepository: SkillRepository? = null,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, sessionManager!!) as T
            }
            modelClass.isAssignableFrom(RadarViewModel::class.java) -> {
                RadarViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(TradeViewModel::class.java) -> {
                TradeViewModel(swapRepository!!) as T
            }
            modelClass.isAssignableFrom(LedgerViewModel::class.java) -> {
                LedgerViewModel(ledgerRepository!!, skillRepository!!) as T
            }
            modelClass.isAssignableFrom(SkillViewModel::class.java) -> {
                SkillViewModel(skillRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
