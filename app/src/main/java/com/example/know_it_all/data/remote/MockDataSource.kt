package com.example.know_it_all.data.remote

import com.example.know_it_all.data.model.AuthData
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.data.model.dto.UserDTO
import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType

object MockDataSource {

    fun login(email: String, password: String): Result<AuthData> {
        return if (email.isNotEmpty() && password.length >= 6) {
            Result.success(AuthData(
                token = "mock_token_12345",
                userId = "mock_user_001"
            ))
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    fun register(name: String, email: String, password: String): Result<AuthData> {
        return if (email.isNotEmpty() && password.length >= 6) {
            Result.success(AuthData(
                token = "mock_token_12345",
                userId = "mock_user_001"
            ))
        } else {
            Result.failure(Exception("Registration failed"))
        }
    }

    fun getNearbyUsers(): List<UserDTO> {
        return listOf(
            UserDTO(
                uid = "user_001",
                name = "Rahul Sharma",
                email = "rahul@example.com",
                latitude = 18.6298,
                longitude = 73.7997,
                trustScore = 4.5f
            ),
            UserDTO(
                uid = "user_002",
                name = "Priya Patel",
                email = "priya@example.com",
                latitude = 18.6350,
                longitude = 73.8050,
                trustScore = 4.8f
            ),
            UserDTO(
                uid = "user_003",
                name = "Amit Kumar",
                email = "amit@example.com",
                latitude = 18.6250,
                longitude = 73.7950,
                trustScore = 3.9f
            )
        )
    }

    @JvmStatic
    fun getActiveSwaps(): List<SwapDTO> {
        return listOf(
            SwapDTO(
                swapId = "swap_001",
                mentorId = "user_001",
                learnerId = "mock_user_001",
                mentorName = "Rahul Sharma",
                skillName = "Python Programming",
                mentorSkillId = 1,
                learnerSkillId = null,
                swapType = SwapType.TOKEN,
                tokenAmount = 10L,
                status = SwapStatus.ACTIVE
            ),
            SwapDTO(
                swapId = "swap_002",
                mentorId = "user_002",
                learnerId = "mock_user_001",
                mentorName = "Priya Patel",
                skillName = "Graphic Design",
                mentorSkillId = 2,
                learnerSkillId = null,
                swapType = SwapType.BARTER,
                tokenAmount = 0L,
                status = SwapStatus.REQUESTED
            )
        )
    }

    fun getSwapHistory(): List<SwapDTO> {
        return listOf(
            SwapDTO(
                swapId = "swap_old_001",
                mentorId = "user_003",
                learnerId = "mock_user_001",
                mentorName = "Amit Kumar",
                skillName = "Excel & Data Analysis",
                mentorSkillId = 3,
                learnerSkillId = null,
                swapType = SwapType.TOKEN,
                tokenAmount = 5L,
                status = SwapStatus.COMPLETED
            )
        )
    }
}