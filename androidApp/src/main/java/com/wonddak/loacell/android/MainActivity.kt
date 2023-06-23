package com.wonddak.loacell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.wonddak.database.AppDataBase
import com.wonddak.database.DriverFactory
import com.wonddak.loacell.Config
import com.wonddak.loacell.android.ui.home.MainContent
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.android.viewModel.LoaCellViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private lateinit var loaCellViewModel: LoaCellViewModel
    private var waitTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
        val config = Config(this)

        loaCellViewModel = ViewModelProvider(
            this,
            LoaCellViewModelFactory(db, config)
        )[LoaCellViewModel::class.java]

        setContent {
            val selectedRoomId by loaCellViewModel.roomId.collectAsState()
            BackHandler(selectedRoomId.isEmpty()) {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    loaCellViewModel.showSnackBar("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
                } else {
                    finish()
                }
            }
            MainContent(db, loaCellViewModel)
        }
    }
}