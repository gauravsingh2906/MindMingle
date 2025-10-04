package com.futurion.apps.mathmingle.data.converters

import androidx.room.TypeConverter


class Converters {
    @TypeConverter
    fun fromString(value: String?): Set<String> {
        if (value.isNullOrEmpty()) return emptySet()
        return value.split(",").map { it.trim() }.toSet()
    }

    @TypeConverter
    fun setToString(set: Set<String>?): String {
        return set?.joinToString(",") ?: ""
    }
}
