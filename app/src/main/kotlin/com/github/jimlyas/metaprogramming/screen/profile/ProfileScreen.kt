package com.github.jimlyas.metaprogramming.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.github.jimlyas.metaprogramming.annotation.Route
import com.github.jimlyas.metaprogramming.data.Profile
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDestination(val data: Profile)

@Route(ProfileDestination::class)
@Composable
internal fun ProfileScreen(
    args: ProfileDestination,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = args.data.name)
        Text(text = args.data.age.toString())
    }
}