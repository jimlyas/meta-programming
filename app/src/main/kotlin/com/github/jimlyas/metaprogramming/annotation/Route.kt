package com.github.jimlyas.metaprogramming.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Route(val navArgs: KClass<*>)
