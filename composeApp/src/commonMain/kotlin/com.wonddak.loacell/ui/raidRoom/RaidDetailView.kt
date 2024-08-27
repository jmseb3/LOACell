package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaidDetailView(
    raidInfo: RaidInfo?,
    onBack: () -> Unit,
) {
    if (raidInfo == null) {
        TextButton(onClick = onBack) {
            Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
        }
    } else {
        val tabs = arrayListOf("All").also { arr ->
            (1..raidInfo.getMaxParty()).forEach {
                arr.add(it.toString())
            }
        }
        val tabIndex = rememberPagerState(pageCount = {
            tabs.size
        })
        val scope = rememberCoroutineScope()
        val partyIndex = arrayOf(0, 4, 8, 12)
        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    raidInfo.title,
                    onBack = onBack
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                TabRow(selectedTabIndex = tabIndex.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = tabIndex.currentPage == index,
                            onClick = {
                                scope.launch {
                                    tabIndex.scrollToPage(index)
                                }
                            }
                        )
                    }
                }
                HorizontalPager(
                    tabIndex,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            Text("0")
                        }

                        1, 2, 3, 4 -> {
                            Text(page.toString())
                        }
                    }
                }
            }
        }
    }
}