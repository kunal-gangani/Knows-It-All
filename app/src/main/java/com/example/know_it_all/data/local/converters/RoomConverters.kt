package com.example.know_it_all.data.local.converters

import androidx.room.TypeConverter
import com.example.know_it_all.data.model.SkillCategory
import com.example.know_it_all.data.model.ProficiencyLevel
import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.SwapType

class RoomConverters {
    @TypeConverter
    fun fromSkillCategory(value: SkillCategory): String = value.name

    @TypeConverter
    fun toSkillCategory(value: String): SkillCategory = SkillCategory.valueOf(value)

    @TypeConverter
    fun fromProficiencyLevel(value: ProficiencyLevel): String = value.name

    @TypeConverter
    fun toProficiencyLevel(value: String): ProficiencyLevel = ProficiencyLevel.valueOf(value)

    @TypeConverter
    fun fromSwapStatus(value: SwapStatus): String = value.name

    @TypeConverter
    fun toSwapStatus(value: String): SwapStatus = SwapStatus.valueOf(value)

    @TypeConverter
    fun fromSwapType(value: SwapType): String = value.name

    @TypeConverter
    fun toSwapType(value: String): SwapType = SwapType.valueOf(value)
}
