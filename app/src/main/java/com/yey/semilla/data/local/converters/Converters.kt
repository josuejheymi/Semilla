package com.yey.semilla.data.local.converters

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    //Le dice a Room cómo guardar un valor booleano en la base de datos.
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    //Le dice a Room cómo leer un valor entero de la base de datos y convertirlo de nuevo a Kotlin.
    fun toBoolean(value: Int): Boolean = value == 1
}