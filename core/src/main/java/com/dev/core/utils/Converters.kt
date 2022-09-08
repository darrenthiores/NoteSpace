package com.dev.core.utils

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    /**List of String to String**/
    @TypeConverter
    fun listOfStringToString(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun stringToListOfString(value: String) = Json.decodeFromString<List<String>>(value)
}