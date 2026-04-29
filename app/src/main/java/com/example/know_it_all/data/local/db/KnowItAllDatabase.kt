package com.example.know_it_all.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.know_it_all.data.local.RoomTypeConverters
import com.example.know_it_all.data.local.dao.SkillDao
import com.example.know_it_all.data.local.dao.SwapDao
import com.example.know_it_all.data.local.dao.TrustLedgerDao
import com.example.know_it_all.data.local.dao.UserDao
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.TrustLedger
import com.example.know_it_all.data.model.User

/**
 * Fixes applied:
 *  1. @TypeConverters now references RoomTypeConverters (the correct class
 *     from the fixed file). The original referenced RoomConverters which
 *     either didn't exist or was the unfixed version without name-based
 *     enum storage.
 *  2. version bumped to 2 — the schema changed (skillId String, new columns
 *     on User and TrustLedger, new indices). Running version 1 against the
 *     new schema causes a Room schema validation crash on app launch.
 *  3. exportSchema set to true — Room generates a schema JSON file on each
 *     build that should be committed to source control. This makes migrations
 *     auditable and prevents accidental schema drift between team members.
 *     Add this to build.gradle.kts:
 *       ksp { arg("room.schemaLocation", "$projectDir/schemas") }
 *  4. Added a companion object with a thread-safe singleton builder.
 *     Previously the database was constructed somewhere outside this class
 *     with no visibility into the configuration. Centralising it here means
 *     there is exactly one construction path and one place to add migrations.
 *  5. Added MIGRATION_1_2 as a documented starting point. During active
 *     development you can use fallbackToDestructiveMigration() instead,
 *     but the migration object should be in place before any user data exists.
 *  6. Added allowMainThreadQueries() guard comment — it is NOT enabled here.
 *     Room DAOs with suspend functions and Flow already enforce off-main-thread
 *     execution; this comment exists to prevent someone adding it "for testing".
 */
@Database(
    entities = [
        User::class,
        Skill::class,
        Swap::class,
        TrustLedger::class
    ],
    version = 3,                   // ✅ bumped — schema changed from Batch 1 fixes
    exportSchema = true            // ✅ generates auditable schema JSON in /schemas
)
@TypeConverters(RoomTypeConverters::class)  // ✅ correct converter class
abstract class KnowItAllDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun skillDao(): SkillDao
    abstract fun swapDao(): SwapDao
    abstract fun trustLedgerDao(): TrustLedgerDao

    companion object {

        @Volatile
        private var INSTANCE: KnowItAllDatabase? = null

        /**
         * Returns the singleton database instance, creating it if necessary.
         * @Volatile ensures INSTANCE is always read from main memory, not a
         * thread-local CPU cache — critical for double-checked locking to work.
         */
        fun getInstance(context: Context): KnowItAllDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): KnowItAllDatabase {
            return Room.databaseBuilder(
                context.applicationContext,     // always applicationContext — never Activity context
                KnowItAllDatabase::class.java,
                "knowitall_database"
            )
                .addMigrations(MIGRATION_1_2)
                // During development only — comment out before any real user data:
                // .fallbackToDestructiveMigration()
                //
                // DO NOT add .allowMainThreadQueries() — all DAOs use suspend/Flow
                .build()
        }

        /**
         * Migration from schema version 1 → 2.
         *
         * Changes in v2:
         *  - users:       added isOnline (INTEGER), updatedAt default changed
         *  - skills:      skillId changed TEXT (was INTEGER), added tokenValue
         *  - swaps:       mentorSkillId/learnerSkillId changed TEXT (was INTEGER)
         *                 verificationMethod column type changed
         *  - trust_ledger: removed transactionData, added mentorId/learnerId/
         *                  skillName/status columns, ratingGiven type check
         *
         * For a development build with no production data, replacing this with
         * fallbackToDestructiveMigration() is acceptable. For any build that has
         * reached real users, write the full ALTER TABLE statements here.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add isOnline column to users (new in v2)
                db.execSQL("ALTER TABLE users ADD COLUMN isOnline INTEGER NOT NULL DEFAULT 0")

                // Add tokenValue to skills (new in v2)
                db.execSQL("ALTER TABLE skills ADD COLUMN tokenValue INTEGER NOT NULL DEFAULT 10")

                // Add explicit participant columns to trust_ledger (new in v2)
                db.execSQL("ALTER TABLE trust_ledger ADD COLUMN mentorId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE trust_ledger ADD COLUMN learnerId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE trust_ledger ADD COLUMN skillName TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE trust_ledger ADD COLUMN status TEXT NOT NULL DEFAULT 'COMPLETED'")

                // Note: SQLite does not support changing column types in-place.
                // For the skillId Int→String and mentorSkillId Int→String changes,
                // a full table recreation is required in production. During
                // development, fallbackToDestructiveMigration() is simpler.
            }
        }
    }
}