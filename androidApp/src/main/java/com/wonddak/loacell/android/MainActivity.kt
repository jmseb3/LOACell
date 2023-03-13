package com.wonddak.loacell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wonddak.loacell.Greeting
import com.wonddak.loacell.LostArkApi
import com.wonddak.loacell.model.CharacterInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var lostArkApi: LostArkApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lostArkApi = LostArkApi()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val scope = rememberCoroutineScope()
                    var itemList :List<CharacterInfo> by remember { mutableStateOf(emptyList()) }
                    LaunchedEffect(true) {
                        scope.launch {
                            delay(2000L)
                            itemList = try {
                               lostArkApi.getCharacterInfo("아이오에스티떡상가즈아")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                emptyList()
                            }
                        }
                    }
                    if (itemList.isEmpty()) {
                        Text(text = "hello")
                    } else {
                        Column() {
                            itemList.forEach {
                                Column() {
                                    Text(text = it.characterName)
                                    Text(text = it.characterClassName)
                                    Text(text = it.characterLevel.toString())
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {

    }
}
