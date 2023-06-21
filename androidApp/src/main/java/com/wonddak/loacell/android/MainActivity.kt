package com.wonddak.loacell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wonddak.database.AppDataBase
import com.wonddak.database.DriverFactory
import com.wonddak.loacell.android.ui.home.MainContent
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.android.viewModel.LoaCellViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private lateinit var loaCellViewModel: LoaCellViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        loaCellViewModel = ViewModelProvider(
            this,
            LoaCellViewModelFactory(db)
        )[LoaCellViewModel::class.java]

        setContent {
            MainContent(db, loaCellViewModel)
        }
    }
}