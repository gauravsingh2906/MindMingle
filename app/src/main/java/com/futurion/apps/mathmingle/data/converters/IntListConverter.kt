package com.futurion.apps.mathmingle.data.converters

import androidx.room.TypeConverter

class IntListConverter {
    @TypeConverter
    fun fromList(list: List<Int>): String = list.joinToString(",")
    @TypeConverter
    fun toList(data: String): List<Int> = if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}
