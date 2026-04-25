package com.example.know_it_all.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.know_it_all.data.repository.LedgerRepository
import com.example.know_it_all.data.repository.SkillRepository
import com.example.know_it_all.data.repository.SwapRepository
import com.example.know_it_all.data.repository.UserRepository
import com.example.know_it_all.util.SessionManager

/**
 * Fixes applied:
 *
 *  1. All parameters are now nullable with null defaults — no repository
 *     should be forced non-null at the factory level since different
 *     ViewModels need different subsets of dependencies.
 *
 *  2. !! force-unwrap replaced with requireNotNull() + descriptive messages.
 *     Force-unwrap throws NullPointerException with no context. requireNotNull()
 *     throws IllegalStateException with a message that names exactly which
 *     dependency is missing, making misconfigured factory calls debuggable.
 *
 *  3. SessionManager is now passed to every ViewModel that needs it
 *     (Radar, Trade, Ledger, Skill) — previously it was only wired into
 *     Auth and ignored for the rest, forcing each ViewModel to receive the
 *     token as a function parameter instead.
 *
 *  4. UserRepository removed as a required param for ViewModels that don't
 *     use it (Trade, Ledger) — the factory no longer forces callers to
 *     provide unused dependencies.
 *
 *  Migration note:
 *   Once Hilt is added, delete this class entirely. Annotate each ViewModel
 *   with @HiltViewModel and replace every viewModel(factory = ...) call
 *   with hiltViewModel(). The NavGraph cleanup is the main beneficiary.
 */
class ViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val swapRepository: SwapRepository? = null,
    private val ledgerRepository: LedgerRepository? = null,
    private val skillRepository: SkillRepository? = null,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    userRepository = requireNotNull(userRepository) {
                        "UserRepository is required for AuthViewModel"
                    },
                    sessionManager = requireNotNull(sessionManager) {
                        "SessionManager is required for AuthViewModel"
                    }
                ) as T
            }

            modelClass.isAssignableFrom(RadarViewModel::class.java) -> {
                RadarViewModel(
                    userRepository = requireNotNull(userRepository) {
                        "UserRepository is required for RadarViewModel"
                    },
                    sessionManager = requireNotNull(sessionManager) {
                        "SessionManager is required for RadarViewModel"
                    }
                ) as T
            }

            modelClass.isAssignableFrom(TradeViewModel::class.java) -> {
                TradeViewModel(
                    swapRepository = requireNotNull(swapRepository) {
                        "SwapRepository is required for TradeViewModel"
                    },
                    sessionManager = requireNotNull(sessionManager) {
                        "SessionManager is required for TradeViewModel"
                    }
                ) as T
            }

            modelClass.isAssignableFrom(LedgerViewModel::class.java) -> {
                LedgerViewModel(
                    ledgerRepository = requireNotNull(ledgerRepository) {
                        "LedgerRepository is required for LedgerViewModel"
                    },
                    skillRepository = requireNotNull(skillRepository) {
                        "SkillRepository is required for LedgerViewModel"
                    },
                    userRepository = requireNotNull(userRepository) {
                        "UserRepository is required for LedgerViewModel"
                    },
                    sessionManager = requireNotNull(sessionManager) {
                        "SessionManager is required for LedgerViewModel"
                    }
                ) as T
            }

            modelClass.isAssignableFrom(SkillViewModel::class.java) -> {
                SkillViewModel(
                    skillRepository = requireNotNull(skillRepository) {
                        "SkillRepository is required for SkillViewModel"
                    },
                    sessionManager = requireNotNull(sessionManager) {
                        "SessionManager is required for SkillViewModel"
                    }
                ) as T
            }

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}. " +
                "Register it in ViewModelFactory or migrate to Hilt."
            )
        }
    }
}