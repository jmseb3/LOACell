package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.Const.navigationToRoom
import com.wonddak.loacell.auth.signOut
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.koinInject

@Composable
fun MainView(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel = koinInject(),
    storeViewModel: StoreViewModel = koinInject(),
    raidViewModel: RaidViewModel = koinInject(),
) {
    LaunchedEffect(authViewModel.initSuccess, authViewModel.user) {
        if (authViewModel.initSuccess) {
            if (authViewModel.user == null) {
                navController.navigate(Const.NAV_LOGIN) {
                    this.launchSingleTop = true
                }
                storeViewModel.stopObserveRoom()
            } else {
                storeViewModel.startObserveRoom(authViewModel.user!!.uid)
            }
            raidViewModel.stopObserveRaidInfo()
        }
    }
    Column(
        modifier = modifier
    ) {
        Text("This is Main with ${authViewModel.user}")
        TextButton(
            onClick = {
                authViewModel.loginHelper.signOut()
            }
        ) {
            Text("Logout")
        }
        LazyColumn {
            items(storeViewModel.roomList) { roomInfo ->
                TextButton(
                    onClick = {
                        navController.navigate(roomInfo.navigationToRoom())
                    }
                ) {
                    Column {
                        Text(roomInfo.title)
                        Text(roomInfo.description)
                    }
                }
            }
        }
    }
}