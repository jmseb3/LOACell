package com.wonddak.loacell.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.raid.RaidView
import com.wonddak.loacell.android.ui.raid.RoomView
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private val loaCellViewModel: LoaCellViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
        lifecycleScope.launch {
            loaCellViewModel.roomId.collect { selectedRoomId ->
                if (selectedRoomId.isNotEmpty()) {
                    FireStoreHelper.observeUsers(selectedRoomId, db)
                    FireStoreHelper.observeRaid(selectedRoomId, db)
                }
            }
        }

        setContent {
            MainContent(db, loaCellViewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        loginHelper.updateUserInfo()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()

    MyApplicationTheme() {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = Color.White,
            bottomBar = {
                MyBottomAppBar(loaCellViewModel)
            },
            topBar = {
                MyTopAppBar(loaCellViewModel = loaCellViewModel)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                val roomList by db.roomInfoQueriesHelper.getALl()
                    .collectAsState(initial = emptyList())

                Column(Modifier.fillMaxSize()) {
                    AnimatedVisibility(selectedRoomId.isEmpty()) {
                        Column() {
                            var show by remember {
                                mutableStateOf(false)
                            }
                            OutlinedButton(onClick = {
                                db.roomInfoQueriesHelper.addRoomInfo(
                                    "123",
                                    "456",
                                    "hxQlFNqieMviWhQPp4HL"
                                )
                            }) {
                                Text(text = "ADD_TestRoom")
                            }
                            RoomView(
                                roomList = roomList,
                                showRoomInfo = { roomId ->
                                    loaCellViewModel.showRoomInfo(roomId)
                                }
                            )
                        }
                    }
                    AnimatedVisibility(selectedRoomId.isNotEmpty()) {
                        if (selectedRoomId.isNotEmpty()) {
                            Column() {
                                RaidView(
                                    db,
                                    loaCellViewModel
                                )
                            }
                        }
                    }
                }
                val context = LocalContext.current
                if (loaCellViewModel.showRoomAdd) {
                    BottomSheetDialog(
                        onDismissRequest = { loaCellViewModel.showRoomAdd = false },
                        properties = BottomSheetDialogProperties(dismissWithAnimation = true),
                    ) {
                        AddRoomSheet() { title, description ->
                            if (title.isNotEmpty()) {
                                FireStoreHelper.addRoomInfo(
                                    title, description
                                ) { id ->
                                    db.roomInfoQueriesHelper.addRoomInfo(
                                        title,
                                        description,
                                        id
                                    )
                                }
                                loaCellViewModel.showRoomAdd = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "title이 비어있습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBottomAppBar(
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()

    BottomAppBar(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedRoomId.isEmpty()) {
                        loaCellViewModel.showRoomDialog()
                    } else {
                        if (loaCellViewModel.tabState == 0) {
                            loaCellViewModel.showRaidAdd = true
                        } else if (loaCellViewModel.tabState == 1) {
                            loaCellViewModel.showUserAdd = true
                        }
                    }
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(2.dp)
            ) {
                Icon(Icons.Filled.Add, null)
            }

        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    TopAppBar(
        title = {},
        actions = {
            
        },
        navigationIcon = {
            AnimatedVisibility(selectedRoomId.isNotEmpty()) {
                IconButton(onClick = { loaCellViewModel.hideRoomInfo() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}