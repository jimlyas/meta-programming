package com.github.jimlyas.metaprogramming.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.jimlyas.metaprogramming.annotation.Route
import com.github.jimlyas.metaprogramming.data.Profile
import com.github.jimlyas.metaprogramming.screen.profile.ProfileDestination
import kotlinx.serialization.Serializable

@Serializable
object ListDestination

@Route(ListDestination::class)
@Composable
internal fun ListScreen(args: ListDestination, navController: NavController) {
    val profiles = remember {
        listOf(
            Profile(1, "Robert Downey Jr.", 60),
            Profile(2, "Scarlett Johansson", 40),
            Profile(3, "Chris Evans", 43),
            Profile(4, "Mark Ruffalo", 57),
            Profile(5, "Jeremy Renner", 54),
            Profile(6, "Chris Hemsworth", 41),
        )
    }

    LazyColumn {
        items(
            items = profiles,
            key = { item -> item.id }
        ) { item ->
            Column(
                modifier = Modifier
                    .clickable { navController.navigate(ProfileDestination(item)) }
                    .padding(start = 16.dp, end = 16.dp, top = 5.dp),
                verticalArrangement = spacedBy(5.dp)
            ) {
                Text(text = item.name)
                Text(text = item.age.toString())

                HorizontalDivider(thickness = 2.dp)
            }
        }
    }
}