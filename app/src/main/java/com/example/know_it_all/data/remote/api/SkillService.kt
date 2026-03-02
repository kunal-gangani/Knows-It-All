package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.Skill
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Query

interface SkillService {
    @POST("skills")
    suspend fun addSkill(
        @Header("Authorization") token: String,
        @Body skill: Skill
    ): ApiResponse<Skill>

    @GET("skills/user/{userId}")
    suspend fun getUserSkills(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiResponse<List<Skill>>

    @GET("skills/search")
    suspend fun searchSkills(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("category") category: String? = null
    ): ApiResponse<List<Skill>>

    @PUT("skills/{skillId}")
    suspend fun updateSkill(
        @Header("Authorization") token: String,
        @Path("skillId") skillId: Int,
        @Body skill: Skill
    ): ApiResponse<Skill>

    @DELETE("skills/{skillId}")
    suspend fun deleteSkill(
        @Header("Authorization") token: String,
        @Path("skillId") skillId: Int
    ): ApiResponse<Map<String, Any>>

    @GET("skills/recommendations")
    suspend fun getRecommendedSkills(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): ApiResponse<List<Skill>>
}
