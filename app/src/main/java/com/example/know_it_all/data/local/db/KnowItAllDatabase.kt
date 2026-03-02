package com.example.know_it_all.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.know_it_all.data.local.converters.RoomConverters
import com.example.know_it_all.data.local.dao.UserDao
import com.example.know_it_all.data.local.dao.SkillDao
import com.example.know_it_all.data.local.dao.SwapDao
import com.example.know_it_all.data.local.dao.TrustLedgerDao
import com.example.know_it_all.data.model.User
import com.example.know_it_all.data.model.Skill
import com.example.know_it_all.data.model.Swap
import com.example.know_it_all.data.model.TrustLedger

@Database(
    entities = [User::class, Skill::class, Swap::class, TrustLedger::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class KnowItAllDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun skillDao(): SkillDao
    abstract fun swapDao(): SwapDao
    abstract fun trustLedgerDao(): TrustLedgerDao
}
