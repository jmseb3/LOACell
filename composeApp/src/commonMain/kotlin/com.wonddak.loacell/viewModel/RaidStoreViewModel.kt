package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import io.github.aakira.napier.Napier

class RaidStoreViewModel() : ViewModel() {

    private var raidListenerRegistration: CommonListenerRegistration? = null

    var raidList: List<RaidInfo> by mutableStateOf(emptyList())
        private set

    fun startObserveRaidInfoLust(
        roomId: String?,
    ) {
        roomId ?: return
        raidListenerRegistration = CommonRaidHelper.observe(roomId) {
            raidList = it
            raidList.forEach {
                Napier.d(tag = "raidInfo") { it.toString() }
            }
        }
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.remove()
        raidList = emptyList()
    }
}