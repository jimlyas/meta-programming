package com.github.jimlyas.metaprogramming.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Int,
    val name: String,
    val age: Int
)
