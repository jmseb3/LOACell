package com.wonddak.loacell.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


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

            }
        )
    }
}