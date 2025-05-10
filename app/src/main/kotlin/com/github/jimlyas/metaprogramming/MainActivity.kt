package com.github.jimlyas.metaprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.jimlyas.graph.appGraph
import com.github.jimlyas.metaprogramming.screen.list.ListDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold { padding ->
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = ListDestination::class,
                    modifier = Modifier.padding(padding)
                ) { appGraph(navController) }
            }
        }
    }
}