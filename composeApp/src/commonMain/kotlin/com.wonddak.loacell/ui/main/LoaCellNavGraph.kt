package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wonddak.loacell.BlockBackButton
import com.wonddak.loacell.Const
import com.wonddak.loacell.auth.signOut
import com.wonddak.loacell.ui.login.LoginView
import com.wonddak.loacell.viewModel.AuthViewModel
import org.koin.compose.koinInject

@Composable
fun LoaCellNavGraph(
    navController: NavHostController,
) {
    val authViewModel: AuthViewModel = koinInject()

    Scaffold(
        topBar = {
            LoaCellTopAppBar(navController)
        },
        bottomBar = {
            LoaCellBottomAppBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Const.NAV_MAIN,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Const.NAV_MAIN) {
                Column {
                    LaunchedEffect(authViewModel.initSuccess, authViewModel.user) {
                        if (authViewModel.user == null) {
                            navController.navigate(Const.NAV_LOGIN) {
                                this.launchSingleTop = true
                            }
                        }
                    }
                    Text("This is Main with ${authViewModel.user}")
                    TextButton(
                        onClick = {
                            authViewModel.loginHelper.signOut()
                        }
                    ) {
                        Text("Logout")
                    }
                }
            }
            composable(route = Const.NAV_LOGIN) {
                LaunchedEffect(authViewModel.user) {
                    if (authViewModel.user != null) {
                        navController.popBackStack()
                    }
                }
                LoginView(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun NavController.isMain(): Boolean {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    return currentRoute?.startsWith(Const.NAV_MAIN) ?: false
}