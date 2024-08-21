package com.wonddak.loacell.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaCellTopAppBar(
    navController: NavHostController,
    storeViewModel: StoreViewModel,
    raidViewModel: RaidViewModel,
) {
    if (navController.isLogin()) {

    } else {
        TopAppBar(
            title = {
                if (navController.isMain()) {
                    Text(text = "LoaCell")
                } else if (navController.isRaidRoom()) {
                    Text(raidViewModel.title)
                }
            },
            actions = {

            },
            navigationIcon = {
                if (!navController.isMain()) {
                    IconButton(
                        onClick = {
                            navController.currentBackStackEntry?.destination?.route?.let {
                                if (it.startsWith(Const.NAV_ROOM)) {
                                    navController.navigate(Const.NAV_MAIN) {
                                        popUpTo(Const.NAV_ROOM) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            }
        )
    }
}