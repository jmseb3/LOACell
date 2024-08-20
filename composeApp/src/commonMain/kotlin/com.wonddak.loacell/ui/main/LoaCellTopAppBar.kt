package com.wonddak.loacell.ui.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.wonddak.loacell.ui.isMain


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaCellTopAppBar(
    navController: NavHostController,
) {
    if (navController.isMain()) {
        TopAppBar(
            title = {
                Text(text = "LoaCell")
            },
            actions = {

            },
            navigationIcon = {

            }
        )
    }
}