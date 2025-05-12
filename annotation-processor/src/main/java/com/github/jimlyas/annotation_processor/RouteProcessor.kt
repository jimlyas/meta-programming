package com.github.jimlyas.annotation_processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.squareup.kotlinpoet.withIndent

class RouteProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private lateinit var destinations: List<KSFunctionDeclaration>

    override fun process(resolver: Resolver): List<KSAnnotated> {
        destinations = resolver
            .getSymbolsWithAnnotation("com.github.jimlyas.metaprogramming.annotation.Route")
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        return emptyList()
    }

    override fun finish() {
        super.finish()
        FileSpec.builder(
            packageName = "com.github.jimlyas.graph",
            fileName = "appGraph"
        )
            .addFunction(
                FunSpec
                    .builder("appGraph")
                    .addModifiers(KModifier.INTERNAL)
                    .receiver(ClassName("androidx.navigation", "NavGraphBuilder"))
                    .addParameter(
                        ParameterSpec
                            .builder(
                                name = "navController",
                                type = ClassName("androidx.navigation", "NavController")
                            )
                            .build()
                    )
                    .addComposeBlock(destinations)
                    .build()
            )
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun FunSpec.Builder.addComposeBlock(destinations: List<KSFunctionDeclaration>) = apply {
        addCode(buildCodeBlock {
            destinations.forEach { destination ->

                val annotation = destination
                    .annotations
                    .find { it.shortName.asString() == "Route" }

                val navArgs = annotation
                    ?.arguments
                    ?.find { it.name?.asString() == "navArgs" }
                    ?.value as KSType

                val unsupportedNavTypes = navArgs.getUnsupportedParameters()

                if (unsupportedNavTypes.isNotEmpty()) {
                    addStatement(
                        "%M<%T>(",
                        MemberName("androidx.navigation.compose", "composable"),
                        navArgs.toTypeName()
                    )

                    withIndent {
                        addStatement("typeMap = mapOf(")

                        withIndent {
                            unsupportedNavTypes.forEach { parameter ->
                                addStatement(
                                    "%M<%T>(),",
                                    MemberName(
                                        "com.github.jimlyas.metaprogramming.util",
                                        "typeMapOf"
                                    ),
                                    parameter
                                )
                            }
                        }

                        addStatement(")")
                    }

                    addStatement(") { entry ->")
                } else {
                    addStatement(
                        "%M<%T> { entry ->",
                        MemberName("androidx.navigation.compose", "composable"),
                        navArgs.toTypeName()
                    )
                }

                withIndent {
                    addStatement(
                        "%M(entry.%M<%T>(), navController)",
                        MemberName(
                            destination.packageName.asString(),
                            destination.simpleName.asString()
                        ),
                        MemberName(
                            "androidx.navigation",
                            "toRoute"
                        ),
                        navArgs.toTypeName()
                    )
                }

                addStatement("}")
            }
        })
    }

    private fun KSType.getUnsupportedParameters() = (declaration as KSClassDeclaration)
        .primaryConstructor
        ?.parameters
        ?.filter { type ->
            val declaredType = type.type.resolve().declaration

            declaredType.qualifiedName?.asString() !in supportedNavTypes
        }
        ?.map { it.type.toTypeName() }
        .orEmpty()
        .distinct()

    private val supportedNavTypes = setOf(
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Float",
        "kotlin.Boolean",
        "kotlin.String",
        "kotlin.Array<kotlin.Int>",
        "kotlin.Array<kotlin.Long>",
        "kotlin.Array<kotlin.Float>",
        "kotlin.Array<kotlin.Boolean>",
        "kotlin.Array<kotlin.String>",
        "kotlin.List<kotlin.Int>",
        "kotlin.List<kotlin.Long>",
        "kotlin.List<kotlin.Float>",
        "kotlin.List<kotlin.Boolean>",
        "kotlin.List<kotlin.String>"
    )
}