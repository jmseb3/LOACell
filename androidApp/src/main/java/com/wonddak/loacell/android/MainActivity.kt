package com.wonddak.loacell.android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.wonddak.loacell.LostArkApi
import com.wonddak.loacell.model.CharacterInfo
import com.wonddak.loacell.model.armories.EquipmentItem
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
                    var equipment : List<EquipmentItem> by remember {
                        mutableStateOf(emptyList())
                    }

                    LaunchedEffect(true) {
                        scope.launch {
                            equipment = try {
                                lostArkApi.getArmoriesEquipment("원소술녀")
                            }catch (e:Exception){
                                e.printStackTrace()
                                emptyList()
                            }
                        }
                    }
                    val scrollState = rememberScrollState()
                    if (equipment.isEmpty()) {
                        Text(text = "hello")
                    } else {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState)
                        ) {
                            if (equipment.isNotEmpty()) {
                                equipment.forEach {
                                Log.i("JWH",it.type)
                                    it.getElixirOptionLevels().let {
                                        if (it.isNotEmpty()) {
                                            it.forEach {
                                                Log.i("JWH",it)
                                                Html(text = it)
                                            }
//                                            Log.i("JWH",it)
//                                            Html(text = it)
                                        }
                                    }
                                }
                             
                            }

                        }

                    }

                }
            }
        }
    }
}

@Composable
fun Html(text: String) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            this.setTextColor(Color.parseColor("#000000"))
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
    })
}
@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {

    }
}
