package com.example.know_it_all.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.know_it_all.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    /**
     * REPLACE strategy enables true upsert — inserting a user that already
     * exists (same uid) updates the cached record. This is the correct
     * behaviour for an offline-first cache that syncs from the network.
     * Without it, a second login or profile refresh throws a constraint
     * violation instead of updating.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)                     // ✅ batch insert for Radar results

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserById(uid: String)                        // ✅ delete by ID (safer than object ref)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()                                    // ✅ used on logout to wipe cache

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    /** One-shot fetch — used when a single guaranteed-fresh read is needed. */
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?

    /** Observed flow — SkillProfileScreen and VaultScreen observe the logged-in user. */
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByIdFlow(uid: String): Flow<User?>

    /** Observe all cached users — used by Radar when GPS is unavailable. */
    @Query("SELECT * FROM users ORDER BY trustScore DESC")
    fun getAllUsers(): Flow<List<User>>

    /**
     * Bounding-box proximity query for the Radar screen.
     *
     * The original implementation is kept and corrected here. Note: this is a
     * rectangular bounding box, not a true circular radius. For a 5km radius
     * at latitude ~18.6° (Pune), 1° lat ≈ 111km and 1° lon ≈ 105km, so:
     *   latDelta  = radiusKm / 111.0  ≈ 0.045 for 5km
     *   lonDelta  = radiusKm / 105.0  ≈ 0.048 for 5km
     * The repository layer computes these deltas before calling this method.
     * True Haversine filtering should be done in the repository after this
     * coarse filter to remove the corners of the bounding box.
     */
    @Query("""
        SELECT * FROM users 
        WHERE latitude  BETWEEN :latMin  AND :latMax 
        AND   longitude BETWEEN :lonMin  AND :lonMax
        ORDER BY trustScore DESC
    """)
    suspend fun getUsersInBoundingBox(                             // ✅ renamed for clarity
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double
    ): List<User>

    /**
     * Online users only — used by the Radar screen's "online now" filter
     * which maps to the pulsing green dot in the UI reference design.
     */
    @Query("""
        SELECT * FROM users 
        WHERE isOnline = 1 
        AND latitude  BETWEEN :latMin  AND :latMax 
        AND longitude BETWEEN :lonMin  AND :lonMax
        ORDER BY trustScore DESC
    """)
    suspend fun getOnlineUsersInBoundingBox(                       // ✅ new — online filter
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double
    ): List<User>

    /**
     * Look up by email — used by the auth flow to check for existing accounts
     * locally before making a network call.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?               // ✅ new

    /**
     * Token balance — observed by the Vault screen header.
     * Returns a Flow<Long?> so the screen reacts to balance changes
     * (e.g. after a swap completes) without re-fetching the full User object.
     */
    @Query("SELECT skillTokenBalance FROM users WHERE uid = :uid")
    fun getTokenBalance(uid: String): Flow<Long?>                  // ✅ new — fine-grained observation
}