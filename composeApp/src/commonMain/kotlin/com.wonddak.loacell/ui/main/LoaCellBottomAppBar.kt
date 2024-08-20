package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wonddak.loacell.ui.isMain
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource


@Composable
fun LoaCellBottomAppBar(
    navController: NavHostController,
) {
    if (navController.isMain()) {
        BottomAppBar(
            floatingActionButton = {
                SmallFloatingActionButton(
                    content = {
                        Icon(Icons.Filled.Add, null)
                    },
                    onClick = {

                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(2.dp),
                )
            },
            actions = {
                Row() {
                    IconButton(
                        {}
                    ) {
                        Icon(
                            painterResource(Res.drawable.refresh),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        )
    }
}