package com.github.jimlyas.metaprogramming.util

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T> serializableNavType(
    isNullableAllowed: Boolean = false
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: Bundle, key: String): T? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(Json.encodeToString(value).encodeSpecialChar())
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString(Uri.decode(value).decodeSpecialChar())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NavType<*>) return false
        if (other::class.java != this::class.java) return false
        if (isNullableAllowed != other.isNullableAllowed) return false
        return true
    }

    private fun String.encodeSpecialChar() =
        this.replace(PERCENT, PERCENT_CHAR).replace(PLUS, PLUS_CHAR)

    private fun String.decodeSpecialChar() =
        this.replace(PERCENT_CHAR, PERCENT).replace(PLUS_CHAR, PLUS)

    private val PLUS_CHAR = "%2B"
    private val PERCENT_CHAR = "%25"
    private val PERCENT = "%"
    private val PLUS = "+"
}

inline fun <reified T> typeMapOf(): Pair<KType, NavType<T>> {
    val type = typeOf<T>()
    return type to serializableNavType<T>(isNullableAllowed = type.isMarkedNullable)
}