package com.example.know_it_all.data.remote.api

import com.example.know_it_all.data.model.ApiResponse
import com.example.know_it_all.data.model.dto.SkillCreateRequest
import com.example.know_it_all.data.model.dto.SkillDTO
import com.example.know_it_all.data.model.dto.SkillEndorseRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Fixes applied:
 *
 *  1. addSkill @Body changed from Skill (Room entity) → SkillCreateRequest DTO.
 *     Sending a Room entity over the network exposes @Entity/@PrimaryKey
 *     annotations to Gson, leaks local DB fields the API doesn't expect,
 *     and couples the network layer to the persistence layer.
 *
 *  2. addSkill response changed from ApiResponse<Skill> → ApiResponse<SkillDTO>.
 *     Same reason — API responses must be DTOs; entity mapping happens in
 *     the repository layer.
 *
 *  3. updateSkill @Path skillId changed Int → String to match the corrected
 *     Skill.skillId type (String UUID). Int was a silent type mismatch that
 *     would produce a 404 on every update request.
 *
 *  4. updateSkill @Body changed from Skill → SkillCreateRequest DTO.
 *
 *  5. updateSkill response changed from ApiResponse<Skill> → ApiResponse<SkillDTO>.
 *
 *  6. deleteSkill @Path skillId changed Int → String.
 *
 *  7. deleteSkill response changed from ApiResponse<Map<String, Any>> →
 *     ApiResponse<Unit>. The repository only checks response.success for
 *     deletes — an untyped map adds no value and requires unsafe casts.
 *
 *  8. getUserSkills response changed from ApiResponse<List<Skill>> →
 *     ApiResponse<List<SkillDTO>>.
 *
 *  9. searchSkills response changed from ApiResponse<List<Skill>> →
 *     ApiResponse<List<SkillDTO>>.
 *
 * 10. getRecommendedSkills response changed from ApiResponse<List<Skill>> →
 *     ApiResponse<List<SkillDTO>>.
 *
 * 11. endorseSkill added — SkillRepository.endorseSkill() calls this.
 *     It was missing from the original, meaning the endorse flow would
 *     crash with a NoSuchMethodError at runtime.
 */
interface SkillService {

    @POST("skills")
    suspend fun addSkill(
        @Header("Authorization") token: String,
        @Body request: SkillCreateRequest           // ✅ DTO, not Room entity
    ): ApiResponse<SkillDTO>                        // ✅ DTO response

    @GET("skills/user/{userId}")
    suspend fun getUserSkills(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiResponse<List<SkillDTO>>                  // ✅ DTO response

    @GET("skills/search")
    suspend fun searchSkills(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("category") category: String? = null
    ): ApiResponse<List<SkillDTO>>                  // ✅ DTO response

    @PUT("skills/{skillId}")
    suspend fun updateSkill(
        @Header("Authorization") token: String,
        @Path("skillId") skillId: String,           // ✅ String UUID, was Int
        @Body request: SkillCreateRequest           // ✅ DTO, not Room entity
    ): ApiResponse<SkillDTO>                        // ✅ DTO response

    @DELETE("skills/{skillId}")
    suspend fun deleteSkill(
        @Header("Authorization") token: String,
        @Path("skillId") skillId: String            // ✅ String UUID, was Int
    ): ApiResponse<Unit>                            // ✅ Unit, not Map<String, Any>

    @GET("skills/recommendations")
    suspend fun getRecommendedSkills(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): ApiResponse<List<SkillDTO>>                  // ✅ DTO response

    @POST("skills/{skillId}/endorse")               // ✅ new — was missing, called by repository
    suspend fun endorseSkill(
        @Header("Authorization") token: String,
        @Path("skillId") skillId: String,
        @Body request: SkillEndorseRequest
    ): ApiResponse<Unit>
}