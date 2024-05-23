package com.wonddak.loacell.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import com.wonddak.loacell.android.ui.home.MainContent
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val loaCellViewModel: LoaCellViewModel by viewModel()
    private var waitTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kakaoIntent(intent)
        setContent {
            BackHandler(loaCellViewModel.roomId.isEmpty()) {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    loaCellViewModel.showSnackBar("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
                } else {
                    finish()
                }
            }
            MainContent()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        kakaoIntent(intent)
    }

    private fun kakaoIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "kakaoeaad613c8a32160c49991040e94170f9") {
                uri.getQueryParameter("uniqueId")?.let { id ->
                    loaCellViewModel.checkByScheme(id)
                }
            }
        }
    }
}